package com.example.myparentalcontrolapp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

public class CustomIntentService extends IntentService {
    private Context context;
    private static CustomIntentService instance;
    /**
     * @deprecated
     */
    public CustomIntentService() {
        super("CustomIntentService");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        BackgroundManager.getInstance().init(this).startService();
        super.onTaskRemoved(rootIntent);
    }


    @Override
    public void onDestroy() {
        BackgroundManager.getInstance().init(this).startService();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        String data = intent.getDataString();
        Log.i("MyBackgroundService", data);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String dataString = intent.getDataString();
        Log.i("CustomIntentService", dataString);

        long endTime = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    Intent i = new Intent(this, ActivityBroadcastReceiver.class);
                    sendBroadcast(i);
                    wait(endTime - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("CustomIntentServiceError", e.getMessage());
                }
            }
        }
    }
}
