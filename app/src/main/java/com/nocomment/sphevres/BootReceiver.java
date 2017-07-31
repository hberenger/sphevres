package com.nocomment.sphevres;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "SPHEVRES::BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_USER_PRESENT)) {
            // Nothing to do : creating the Application class starts the service
            Log.w(TAG, "Boot/unlock detected ! Application started if not started yet");
        }
    }
}
