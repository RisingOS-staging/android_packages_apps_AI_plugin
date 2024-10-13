package it.dhd.oxygencustomizer.aiplugin.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.topjohnwu.superuser.Shell;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (!Boolean.TRUE.equals(Shell.isAppGrantedRoot())) {
                Shell.getShell().isRoot();
                }
        }
    }
}
