package com.ask.iba_by.ask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;

public class Worker extends androidx.work.Worker {

    static final String TAG = "workmng";

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @SuppressLint("NewApi")
    @NonNull
    @Override
    public WorkerResult doWork() {
        Log.d(TAG, "doWork: start");
        sendNotification("Worker");

        Intent serviceIntent = new Intent(this.getApplicationContext(), WorkerService.class);

        String valueId =  getInputData().getString("id","");

        //insert values(user's id) to transfer to WorkerService
        serviceIntent.putExtra("inputExtra", "input");
        serviceIntent.putExtra("id", valueId);

        //start WorkerService, which will send coordinates

        ContextCompat.startForegroundService(this.getApplicationContext(), serviceIntent);

        Log.d(TAG, "doWork: end");

        return WorkerResult.SUCCESS;
    }


    //create notification
    //first for sdk < 26
    //second for sdk >=26

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(String message) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            NotificationManager notifManager = (NotificationManager)this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "0");
            Intent intent = new Intent(this.getApplicationContext(), AskActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
            builder.setContentTitle("Ask")                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(this.getApplicationContext().getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker("Ask")
                    .setLights(0xff0000ff, 5000, 5000)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);

            Notification notification = builder.build();
            notifManager.notify(0, notification);
        } else {
            NotificationManager notifManager = (NotificationManager)this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel("0");
            if (mChannel == null) {
                mChannel = new NotificationChannel("0", "Ask", importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "0");
            Intent intent = new Intent(this.getApplicationContext(), AskActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
            builder.setContentTitle("Ask")                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(this.getApplicationContext().getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker("Ask")
                    .setLights(0xff0000ff, 5000, 5000)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            Notification notification = builder.build();
            notifManager.notify(0, notification);
        }


    }



    @Override
    public void onStopped() {
        Log.d(TAG, "doWork: stop");
        super.onStopped();

    }
}