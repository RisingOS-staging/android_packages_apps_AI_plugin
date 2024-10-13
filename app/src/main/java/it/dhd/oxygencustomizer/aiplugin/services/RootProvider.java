package it.dhd.oxygencustomizer.aiplugin.services;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ipc.RootService;
import com.topjohnwu.superuser.nio.FileSystemManager;

import java.util.List;

import it.dhd.oxygencustomizer.aiplugin.IRootProviderService;

public class RootProvider extends RootService {
    /**
     * @noinspection unused
     */
    String TAG = getClass().getSimpleName();

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return new RootServicesIPC();
    }

    /**
     * @noinspection RedundantThrows
     */
    class RootServicesIPC extends IRootProviderService.Stub {
        int mLSPosedMID = -1;

        @Override
        public IBinder getFileSystemService() {
            return FileSystemManager.getService();
        }
    }
}
