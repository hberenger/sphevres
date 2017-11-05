package com.nocomment.sphevres;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;


public class BeaconDetector {
    private BeaconManager beaconManager;
    private BeaconRegion mRegion;

    BeaconDetector() {
        mRegion = new BeaconRegion(
                "Globes",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                null, null);
    }

    void start(final Context context) {
        beaconManager = new BeaconManager(context);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(mRegion);
                beaconManager.startRanging(mRegion);
            }
        });

        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
            @Override
            public void onEnteredRegion(BeaconRegion beaconRegion, List<Beacon> beacons) {
                Log.d("beacon", "I'm in");
            }

            @Override
            public void onExitedRegion(BeaconRegion beaconRegion) {
                Log.d("beacon", "I'm out");
            }
        });

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
                if (beacons != null && !beacons.isEmpty()) {
                    boolean found = false;
                    for (Beacon beacon : beacons) {
                        Log.d("Beacon", "Beacon " + beacons.indexOf(beacon) + " detected with power=" + beacon.getMeasuredPower() + " rssi=" + beacon.getRssi());
                        if (beacon.getRssi() > -80) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        Intent intent = new Intent();
                        intent.setAction(PowerConnectionReceiver.PROXIMITY_INTENT);
                        context.sendBroadcast(intent);
                    }
                }
            }
        });
    }

    public void stop() {
        beaconManager.stopMonitoring(mRegion.getIdentifier());
        beaconManager.stopRanging(mRegion);
    }
}
