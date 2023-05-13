package com.example.myparentalcontrolapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.myparentalcontrolapp.FinishScreenActivity;
import com.example.myparentalcontrolapp.R;

import javax.annotation.Nullable;

public class MyForegroundService extends Service {
    private static final String CHANNEL_ID = "MyForegroundServiceChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, FinishScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Parental Control App")
                .setContentText("Running in the background...")
                .setSmallIcon(R.drawable.male)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        // Start your JobIntentService here
        Intent jobIntent = new Intent(this, CustomJobIntentService.class);
        CustomJobIntentService.enqueueWork(this, jobIntent);

        Log.i("CMyForegroundService", "background service started");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "MyForegroundService Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

            Log.i("CMyForegroundService", "notification channel created");
        }
    }
}