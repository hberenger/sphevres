package com.nocomment.sphevres;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;

import org.gearvrf.GVRActivity;
import org.gearvrf.utility.DockEventReceiver;

public class MainActivity extends GVRActivity {

    private AmbisonicPlayer player;
    private DockEventReceiver dockEventReceiver;
    private BeaconDetector mBeaconDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeDialog);

        Thread.setDefaultUncaughtExceptionHandler(new RestartExceptionHandler(this));

        if (getIntent().getBooleanExtra("crash", false)) {
            Toast.makeText(this, "App restarted after crash", Toast.LENGTH_SHORT).show();
        } else {
            // crash after 10 sec - to test the Restart exception Handler
            // new Handler().postDelayed(new Runnable() {
            //    @Override
            //    public void run() {
            //        throw new NullPointerException();
            //    }
            //}, 10000);
        }

        PowerConnectionReceiver.checkBattery(this);

        //player = new AmbisonicPlayer(this.getApplicationContext());

        setMain(new MainMontreal(player), "gvr.xml");

        // Detecting when the headset is used by someone
        dockEventReceiver = new DockEventReceiver(this, new Runnable() {
            @Override
            public void run() {
//                MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.neon);
//                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mPlayer.start();

                if (player != null) {
                    player.rewind();
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                // nothing to do
//                MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.neon);
//                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mPlayer.start();
            }
        });
        dockEventReceiver.start();

        mBeaconDetector = new BeaconDetector();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (player != null) {
            player.start();
        }
        mBeaconDetector.start(getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBeaconDetector.stop(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        if (player != null) {
            player.rewind();
        }
    }
}
