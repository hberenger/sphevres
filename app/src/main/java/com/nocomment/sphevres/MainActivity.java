package com.nocomment.sphevres;

import android.os.Bundle;

import org.gearvrf.GVRActivity;

public class MainActivity extends GVRActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMain(new Main(), "gvr.xml");

        PowerConnectionReceiver.checkBattery(this);
    }

}
