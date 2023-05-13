package com.example.myparentalcontrolapp.services;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class CustomJobIntentService extends JobIntentService {
    private static final int JOB_ID = 15462;

    public static void enqueueWork(Context ctx, Intent work) {
        Log.i("CustomJobIntentService", "enqueueWork");
        enqueueWork(ctx, CustomJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i("CustomJobIntentService", "onhandle");
        executeService();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("AppDestroy", "on task remove job intent");
//        BackgroundManager.getInstance().init(this).startService();
        restartService();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.i("AppDestroy", "on destroy job intent");
//        BackgroundManager.getInstance().init(this).startService();
        restartService();
        super.onDestroy();
    }

    private  void restartService()
    {
        Intent intent = new Intent(this, CustomJobIntentService.class);
        CustomJobIntentService.enqueueWork(this, intent);
    }

    private void executeService() {
        long endTime = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    Intent intent = new Intent(this, ActivityBroadcastReceiver.class);
                    sendBroadcast(intent);
                    wait(endTime - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("CustomJobIntentService", e.getMessage());
                }
            }
        }
    }




}
