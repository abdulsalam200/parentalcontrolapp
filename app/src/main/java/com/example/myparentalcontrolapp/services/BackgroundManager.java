package com.example.myparentalcontrolapp.services;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BackgroundManager {
    private static final int period = 15 * 1000;
    private static final int ALARM_ID = 159874;
    private static BackgroundManager instance;
    private Context context;

    public static BackgroundManager getInstance() {
        if (instance == null) {
            instance = new BackgroundManager();
        }
        return instance;
    }

    public BackgroundManager init(Context ctx) {
        context = ctx;
        return this;
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Log.i("MyBackground", manager.toString());
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i("MyBackground",serviceClass.getName());
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startService() {
        Log.i("MyBackground", "start service called");
        Log.i("MyBackground", "Build: "+ Build.VERSION.SDK_INT+" - "+Build.VERSION_CODES.O);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isServiceRunning(CustomJobIntentService.class)) {
                Intent intent = new Intent(context, CustomJobIntentService.class);
                CustomJobIntentService.enqueueWork(context, intent);
            }
        } else {
            if (!isServiceRunning(CustomIntentService.class)) {
                context.startService(new Intent(context, CustomIntentService.class));
            }
        }
    }

    public void stopService(Class<?> serviceClass) {
        if (isServiceRunning(serviceClass)) {
            context.stopService(new Intent(context, serviceClass));
        }
    }
}
