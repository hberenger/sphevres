package com.nocomment.sphevres;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;


public class MyApplication extends Application {

    private static final String TAG = "SPHEVRES::MyApplication";

    public static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.i(TAG, "App class created !");

        if (!isAngelProcess()) {
            Intent service = new Intent(this.getApplicationContext(), AngelService.class);
            service.setAction(AngelService.ACTION_START);
            Log.i(TAG, "App starts service !");
            startService(service);
        }
    }
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static MyApplication getInstance() {
        return instance;
    }

    private String processName() {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        if (infos != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : infos) {
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return "?";
    }

    private boolean isAngelProcess() {
        String processName = processName();
        String angelProcessName = getString(R.string.angel_process_name);
        return processName.contains(angelProcessName);
    }

}
