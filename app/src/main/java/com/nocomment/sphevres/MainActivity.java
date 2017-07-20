package com.nocomment.sphevres;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import org.gearvrf.GVRActivity;

public class MainActivity extends GVRActivity {

    private AmbisonicPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new RestartExceptionHandler(this));

        if (getIntent().getBooleanExtra("crash", false)) {
            Toast.makeText(this, "App restarted after crash", Toast.LENGTH_SHORT).show();
        } else {
            // crash after 10 sec - to test the Restart exception Handler
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    throw new NullPointerException();
                }
            }, 10000);
        }

        PowerConnectionReceiver.checkBattery(this);

        player = new AmbisonicPlayer(this.getApplicationContext());

        setMain(new Main(player), "gvr.xml");
    }

    @Override
    protected void onStart() {
        super.onStart();

        player.start();
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
        if (player != null) {
            player.rewind();
        }
    }
}
