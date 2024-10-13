package it.dhd.oxygencustomizer.aiplugin.receivers;

import static it.dhd.oxygencustomizer.aiplugin.utils.Constants.ACTION_EXTRACT_FAILURE;
import static it.dhd.oxygencustomizer.aiplugin.utils.Constants.ACTION_EXTRACT_SUBJECT;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ipc.RootService;
import com.topjohnwu.superuser.nio.ExtendedFile;
import com.topjohnwu.superuser.nio.FileSystemManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import it.dhd.oxygencustomizer.aiplugin.BuildConfig;
import it.dhd.oxygencustomizer.aiplugin.IRootProviderService;
import it.dhd.oxygencustomizer.aiplugin.services.RootProvider;
import it.dhd.oxygencustomizer.aiplugin.utils.BitmapSubjectSegmenter;

public class SubjectExtractionReceiver extends BroadcastReceiver {

    private ServiceConnection mCoreRootServiceConnection;
    private IRootProviderService mRootServiceIPC = null;

    private String mSourcePath = null;
    private String mDestinationPath = null;
    private String mSenderPackage = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(ACTION_EXTRACT_SUBJECT)) return;

        Log.d("SubjectExtractionReceiver", "Received intent");

        mSourcePath = intent.getStringExtra("sourcePath");
        mDestinationPath = intent.getStringExtra("destinationPath");
        mSenderPackage = intent.getPackage();

        if (mSourcePath == null || mDestinationPath == null) {
            Intent failureIntent = new Intent();
            failureIntent.setAction(ACTION_EXTRACT_FAILURE);
            failureIntent.setPackage(mSenderPackage);
            Log.d("SubjectExtractionReceiver", "Invalid source or destination path");
            failureIntent.putExtra("error", "Invalid source or destination path");
            context.sendBroadcast(failureIntent);
            return;
        }

        startRootService(context);

    }


    private void startRootService(Context context) {
        // Start RootService connection
        Intent intent = new Intent(context, RootProvider.class);
        mCoreRootServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mRootServiceIPC = IRootProviderService.Stub.asInterface(service);
                try {
                    onRootServiceStarted(context);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mRootServiceIPC = null;
            }
        };
        RootService.bind(intent, mCoreRootServiceConnection);
    }

    private void onRootServiceStarted(Context context) throws RemoteException {
        Log.d("SubjectExtractionReceiver", "RootService started");

        FileSystemManager remoteFS = null;
        try {
            remoteFS = FileSystemManager.getRemote(mRootServiceIPC.getFileSystemService());
        } catch (RemoteException e) {
            // Handle errors
        }
        ExtendedFile sourceFile = remoteFS.getFile(mSourcePath);
        if (!sourceFile.exists()) {
            Intent failureIntent = new Intent();
            failureIntent.setAction(ACTION_EXTRACT_FAILURE);
            failureIntent.setPackage(mSenderPackage);
            Log.d("SubjectExtractionReceiver", "Source file does not exist!");
            failureIntent.putExtra("error", "Source file does not exist!");
            context.sendBroadcast(failureIntent);
            return;
        }


        InputStream inputStream = null;
        try {
            inputStream = sourceFile.newInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap != null) {
                new BitmapSubjectSegmenter(context).segmentSubjectFromJava(bitmap, new BitmapSubjectSegmenter.SegmentResultListener() {
                    @Override
                    public void onSuccess(@Nullable Bitmap result) {
                        Shell.cmd("rm -rf " + mDestinationPath).exec();
                        try {
                            File tempFile = File.createTempFile("lswt", ".png");

                            Log.d("SubjectExtractionReceiver","extractSubject: " + tempFile.getAbsolutePath() + " -> " + mDestinationPath);

                            FileOutputStream outputStream = new FileOutputStream(tempFile);
                            result.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                            outputStream.close();
                            result.recycle();

                            Shell.cmd("cp -F " + tempFile.getAbsolutePath() + " " + mDestinationPath).exec();
                            Shell.cmd("chmod 644 " + mDestinationPath).exec();
                            Log.d("SubjectExtractionReceiver", "onSuccess: BitmapSubjectSegmenter " + mDestinationPath);

                            Intent successIntent = new Intent();
                            successIntent.setAction(ACTION_EXTRACT_SUBJECT);
                            successIntent.setPackage(mSenderPackage);
                            context.sendBroadcast(successIntent);
                        } catch (Throwable t) {
                            Log.e("SubjectExtractionReceiver", "onSuccess: BitmapSubjectSegmenter", t);
                        }
                    }

                    @Override
                    public void onFail(@NonNull Exception e) {
                        Intent failureIntent = new Intent();
                        failureIntent.setAction(ACTION_EXTRACT_FAILURE);
                        failureIntent.setPackage(mSenderPackage);
                        Log.e("SubjectExtractionReceiver", "Failed to extract subject", e);
                        failureIntent.putExtra("error", e.getMessage());
                        context.sendBroadcast(failureIntent);
                    }
                });
            } else {
                Log.d("SubjectExtractionReceiver", "Failed to decode bitmap");
            }
        } catch (Exception e) {
            Log.e("SubjectExtractionReceiver", "Failed to decode bitmap", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
