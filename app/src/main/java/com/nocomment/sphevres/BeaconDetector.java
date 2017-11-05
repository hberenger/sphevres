package com.nocomment.sphevres;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;


public class BeaconDetector {

    public static final String PROXIMITY_INTENT = "com.nocomment.sphevres.beacon.PROXIMITY";

    private BeaconManager beaconManager;
    private BeaconRegion mRegion;

    private BroadcastReceiver mBroadcastReceiver;
    private boolean receiverRegistered;
    private boolean ready;

    BeaconDetector() {
        mRegion = new BeaconRegion(
                "Globes",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                null, null);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                    Log.d("beacon", "Monitoring stopped !");
                    beaconManager.stopRanging(mRegion);
                } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                    Log.d("beacon", "Try to start monitoring");
                    if (ready) {
                        // Toast.makeText(context, "beacon : start ranging", Toast.LENGTH_SHORT).show();
                        beaconManager.startRanging(mRegion);
                        Log.d("beacon", "Ranging started !");
                    }
                }
            }
        };
    }

    void start(final Context context) {
        beaconManager = new BeaconManager(context);
        beaconManager.setForegroundScanPeriod(1500, 10);
        beaconManager.setBackgroundScanPeriod(5000, 25000); // default values

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        context.registerReceiver(mBroadcastReceiver, filter);
        receiverRegistered = true;

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(mRegion);
                ready = true;
                if (!isConnected(context)) {
                    Log.d("beacon", "Ranging fresh start !");
                    beaconManager.startRanging(mRegion);
                }
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
                        if (beacon.getRssi() > -82) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        Intent intent = new Intent();
                        intent.setAction(BeaconDetector.PROXIMITY_INTENT);
                        context.sendBroadcast(intent);
                    }
                }
            }
        });
    }

    public void stop(Context context) {
        Log.d("beacon", "Detector stoooopped !");
        context.unregisterReceiver(mBroadcastReceiver);
        receiverRegistered = false;
        beaconManager.stopMonitoring(mRegion.getIdentifier());
        beaconManager.stopRanging(mRegion);
        ready = false;
    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }
}
