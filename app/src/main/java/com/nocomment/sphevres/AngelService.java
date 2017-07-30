package com.nocomment.sphevres;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class AngelService extends Service {

    public static final String ACTION_START = "start";
    public static final String ACTION_CLOSE = "close";

    private static final String TAG = "SPHEVRES::AngelService";

    public Handler handler = null;
    public Runnable runnable = null;
    private static final int kNotificationID = 1729;
    private int checkCount = 0;
    private int respawnCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Service created !");

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Log.d(TAG, "Service main loop");
                ActivityManager activityManager = (ActivityManager) getSystemService (Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> tasksInfos = activityManager.getRunningTasks(Integer.MAX_VALUE);

                checkCount++;

                if (!isMainAppRunning(tasksInfos)) {
                    Log.w(TAG, "Main app not running... respawning !");
                    respawnCount++;

                    Intent mainIntent = new Intent(AngelService.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(mainIntent);
                }

                updateNotification();

                handler.postDelayed(runnable, 10000);
            }
        };
    }

    private boolean isMainAppRunning(List<ActivityManager.RunningTaskInfo> tasksInfos) {
        if (tasksInfos == null) {
            return false;
        }
        for (final ActivityManager.RunningTaskInfo tasksInfo : tasksInfos) {
            if (tasksInfo.topActivity != null) {
                String topActivityName = tasksInfo.topActivity.getClassName();
                if (topActivityName.contains("sphevres.MainActivity")
                        && tasksInfo.numRunning > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = (intent != null) ?  intent.getAction() : "null";
        Log.i(TAG, "Service received 'onStart' with action : " + action);

        if (action.equals(ACTION_CLOSE)) {
            stopForeground(true);
            stopSelf();
        } else if (action.equals(ACTION_START)) {
            Notification notification = buildNotification();

            startForeground(AngelService.kNotificationID, notification);

            handler.postDelayed(runnable, 10000);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // PRIVATE

    private Notification buildNotification() {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeIntent = new Intent(this, AngelService.class);
        closeIntent.setAction(ACTION_CLOSE);
        PendingIntent pendingCloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        NotificationCompat.Action closeAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_stat_close, "Close service", pendingCloseIntent).build();

        String msg = "I'm your guardian angel!"
                + System.getProperty("line.separator")
                + "checks: " + checkCount
                + " / respawns:" + respawnCount;

        return new NotificationCompat.Builder(this)
                .setContentTitle("Sphevres")
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_notif_icon)
                //.setContentIntent(pendingIntent) // PendingIntent to be sent when the notification is clicked
                .addAction(closeAction)
                .build();
    }

    private void updateNotification() {
        Notification notification = buildNotification();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(AngelService.kNotificationID, notification);
    }
}
