package com.nocomment.sphevres;

import android.os.Bundle;

import org.gearvrf.GVRActivity;

public class MainActivity extends GVRActivity {

    private AmbisonicPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
