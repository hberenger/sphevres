package com.nocomment.sphevres;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.BatteryManager;

public class PowerConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent batteryStatus) {
        PowerConnectionReceiver.readBatteryInfo(batteryStatus);

        if (batteryStatus.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.chime);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.start();
        } else if (batteryStatus.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.ahem);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.start();
        }
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
}
