package com.nocomment.sphevres;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.Log;

public class PowerConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = "SPHEVRES::PowerRcv";

    private static long lastConnectionTimestamp = 0;
    private static long quickConnectionCount = 0;

    public static final String PROXIMITY_INTENT = "com.nocomment.sphevres.beacon.PROXIMITY";


    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            // PowerConnectionReceiver.readBatteryInfo(intent);
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(false);

            playAlert(context, R.raw.chime);
            long d = countConnectionEvents();

            // Toast.makeText(context, "connectionCount = " + quickConnectionCount + "(since " + d +")", Toast.LENGTH_SHORT).show();

            checkManualStart(context);

        } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(true);
            audioManager.setStreamVolume (AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);

            playAlert(context, R.raw.ahem);
        } else if (intent.getAction().equals(PROXIMITY_INTENT)) {
            Log.d("beacon - receiver", "beacon at close range");
        }
    }

    private void playAlert(Context context, int id) {
        final MediaPlayer mPlayer = MediaPlayer.create(context, id);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.start();
            }
        }, 2000);
    }

    static void checkBattery(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        PowerConnectionReceiver.readBatteryInfo(batteryStatus);
    }

    private static void readBatteryInfo(Intent batteryStatus) {
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
    }

    private long countConnectionEvents() {
        long now = System.currentTimeMillis();
        long delay = now - lastConnectionTimestamp;

        if (lastConnectionTimestamp == 0 || delay > 10000) {
            lastConnectionTimestamp = now;
            quickConnectionCount = 0;
        } else {
            quickConnectionCount++;
        }
        return delay;
    }

    private void checkManualStart(Context context) {
        if (quickConnectionCount != 3) {
            return;
        }
        Context appContext = context.getApplicationContext();
        Intent service = new Intent(appContext, AngelService.class);
        service.setAction(AngelService.ACTION_START);
        Log.w(TAG, "Manual service start !");
        appContext.getApplicationContext().startService(service);

        Intent mainIntent = new Intent(appContext, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        Log.w(TAG, "Manual app start !");
        appContext.startActivity(mainIntent);
    }
}
