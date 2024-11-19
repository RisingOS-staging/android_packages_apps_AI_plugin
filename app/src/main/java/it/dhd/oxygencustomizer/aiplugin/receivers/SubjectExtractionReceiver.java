package it.dhd.oxygencustomizer.aiplugin.receivers;

import static it.dhd.oxygencustomizer.aiplugin.utils.Constants.ACTION_EXTRACT_FAILURE;
import static it.dhd.oxygencustomizer.aiplugin.utils.Constants.ACTION_EXTRACT_SUCCESS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.app.WallpaperManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import it.dhd.oxygencustomizer.aiplugin.interfaces.SegmenterResultListener;
import it.dhd.oxygencustomizer.aiplugin.utils.BitmapSubjectSegmenter;
import it.dhd.oxygencustomizer.aiplugin.utils.SubjectSegmenter;

public class SubjectExtractionReceiver extends BroadcastReceiver {

    private String mSenderPackage = null;

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        Log.d("SubjectExtractionReceiver", "Received intent");
        mSenderPackage = intent.getPackage();
        Log.d("SubjectExtractionReceiver", "mSenderPackage: " + mSenderPackage);
        startRemove(context);
    }

    private void startRemove(Context context) {
        try {
            Bitmap wallpaperBitmap = getWallpaperBitmap(context);
            if (wallpaperBitmap == null) {
                sendError(context, "Failed to retrieve wallpaper");
                return;
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int aiMode = Integer.parseInt(prefs.getString("ai_mode", "0"));
            Log.d("SubjectExtractionReceiver", "AI Mode: " + aiMode);
            if (aiMode == 1) {
                Log.d("SubjectExtractionReceiver", "Using SubjectSegmenter " + Integer.parseInt(prefs.getString("ai_model", "0")));
                new SubjectSegmenter(context, Integer.parseInt(prefs.getString("ai_model", "0")), new SegmenterResultListener() {
                    @Override
                    public void onSegmentationResult(Bitmap result) {
                        sendSubjectResult(context, result);
                    }
                    @Override
                    public void onSegmentationError(Exception e) {
                        sendError(context, e.getMessage());
                    }
                }).removeBackground(wallpaperBitmap);
            } else {
                new BitmapSubjectSegmenter(context).segmentSubjectFromJava(wallpaperBitmap, new SegmenterResultListener() {
                    @Override
                    public void onSegmentationResult(@Nullable Bitmap result) {
                        sendSubjectResult(context, result);
                    }
                    @Override
                    public void onSegmentationError(@NonNull Exception e) {
                        sendError(context, e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            Log.e("SubjectExtractionReceiver", "Error processing wallpaper", e);
            sendError(context, "Error processing wallpaper");
        }
    }

    private Bitmap getWallpaperBitmap(Context context) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            if (wallpaperDrawable == null) {
                return null;
            }
            Bitmap wallpaperBitmap = Bitmap.createBitmap(wallpaperDrawable.getIntrinsicWidth(),
                    wallpaperDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(wallpaperBitmap);
            wallpaperDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            wallpaperDrawable.draw(canvas);
            return wallpaperBitmap;
        } catch (Exception e) {
            Log.e("SubjectExtractionReceiver", "Failed to get wallpaper", e);
            return null;
        }
    }

    private void sendSubjectResult(Context context, Bitmap result) {
        if (result == null) {
            sendError(context, "Failed to extract subject");
            return;
        }
        try {
            Intent successIntent = new Intent();
            successIntent.setAction(ACTION_EXTRACT_SUCCESS);
            successIntent.setPackage(mSenderPackage);
            successIntent.putExtra("subjectImage", result);
            context.sendBroadcast(successIntent);
            Log.d("SubjectExtractionReceiver", "Subject extraction successful");
        } catch (Exception e) {
            Log.e("SubjectExtractionReceiver", "Error sending subject result", e);
            sendError(context, "Error sending subject result");
        }
    }

    private void sendError(Context context, String errorMessage) {
        Intent failureIntent = new Intent();
        failureIntent.setAction(ACTION_EXTRACT_FAILURE);
        failureIntent.setPackage(mSenderPackage);
        Log.e("SubjectExtractionReceiver", errorMessage);
        failureIntent.putExtra("error", errorMessage);
        context.sendBroadcast(failureIntent);
    }
}