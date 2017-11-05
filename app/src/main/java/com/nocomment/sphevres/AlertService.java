package com.nocomment.sphevres;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


public class AlertService extends Service {

    MediaPlayer mPlayer;
    BroadcastReceiver mBroadcastReceiver;
    boolean receiverRegistered;
    Timer timer;
    private Handler mHandler = new Handler();
    long lastCloseBeaconTimestamp = -1;
    long startTime = -1;

    boolean playing = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = MediaPlayer.create(this, R.raw.chime);
        mPlayer.setLooping(true); // Set looping
        mPlayer.setVolume(100,100);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        timer = new Timer();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BeaconDetector.PROXIMITY_INTENT)) {
                    Log.d("beacon - service", "beacon at close range");
                    // Toast.makeText(context, "beacon at close range", Toast.LENGTH_SHORT).show();
                    lastCloseBeaconTimestamp = System.currentTimeMillis();
                }
            }
        };
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("coucou", "started");
        lastCloseBeaconTimestamp = -1;
        startTime = System.currentTimeMillis();
        registerReceiver(mBroadcastReceiver, new IntentFilter(BeaconDetector.PROXIMITY_INTENT));
        receiverRegistered = true;
        mPlayer.start();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Log.d("beacon", "alert : time from start = " + (now - startTime) + " time from last ts =" + (now - lastCloseBeaconTimestamp));
                if ( (lastCloseBeaconTimestamp < 0 && (now - startTime) > 4500)
                    || (lastCloseBeaconTimestamp > 0 && (now - lastCloseBeaconTimestamp) > 4500)) {
                    // launch alarm
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!mPlayer.isPlaying()) {
                                mPlayer.start();
                            }
                        }
                    });
                } else if (lastCloseBeaconTimestamp > 0 && (now - lastCloseBeaconTimestamp) < 4500) {
                    // stop alarm
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mPlayer.isPlaying()) {
                                mPlayer.pause();
                            }
                        }
                    });
                }
            }
        }, 0, 1000);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d("coucou", "destroy begin");
        timer.cancel();
        timer = null;
        unregisterReceiver(mBroadcastReceiver);
        receiverRegistered = false;
        mPlayer.stop();
        mPlayer.reset();
        mPlayer.release();
        Log.d("coucou", "destroy end");
    }

    private void playAlert(int id) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.start();
            }
        }, 2000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
