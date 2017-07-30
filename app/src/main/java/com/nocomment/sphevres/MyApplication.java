package com.nocomment.sphevres;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class MyApplication extends Application {

    private static final String TAG = "SPHEVRES::MyApplication";

    public static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Intent service = new Intent(this.getApplicationContext(), AngelService.class);
        service.setAction("start"); // $$$$
        Log.i(TAG, "App starts service !");
        startService(service);
    }
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static MyApplication getInstance() {
        return instance;
    }

}
