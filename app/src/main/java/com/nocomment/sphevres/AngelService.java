package com.nocomment.sphevres;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class AngelService extends Service {

    public Handler handler = null;
    public Runnable runnable = null;
    private int msgId = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                ActivityManager activityManager = (ActivityManager) getSystemService (Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> tasksInfos = activityManager.getRunningTasks(Integer.MAX_VALUE);

                if (!isMainAppRunning(tasksInfos)) {
                    Log.e("TOTO", "Main app not running... rebooting !");
                    Intent mainIntent = new Intent(AngelService.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(mainIntent);
                }

                // $$$$ TODO : update notif msg

                Toast.makeText(AngelService.this, "Service is still running (" + (msgId++) + ")", Toast.LENGTH_LONG).show();
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
        if (intent.getAction().equals("close")) { // $$$$ constant
            stopForeground(true);
            stopSelf();
            // $$$$ TODO ce serait bien de kill l'app
        } else if (intent.getAction().equals("start")) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent closeIntent = new Intent(this, AngelService.class);
            closeIntent.setAction("close");
            PendingIntent pendingCloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

            Notification.Action closeAction =
                    new Notification.Action.Builder(R.drawable.ic_close, "Close service", pendingCloseIntent).build();

            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Sphevres")
                    .setContentText("I'm your guardian angel!")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .addAction(closeAction)
                    .build();

            startForeground(1729, notification);

            handler.postDelayed(runnable, 10000);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
