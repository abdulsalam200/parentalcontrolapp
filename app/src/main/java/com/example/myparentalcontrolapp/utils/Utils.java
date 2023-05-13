package com.example.myparentalcontrolapp.utils;

import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Utils {
    UsageStatsManager usageStatsManager;
    private final Context context;
    public static final String TAG = "abd";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");

    public Utils(Context context) {
        this.context = context;
    }

    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        return usageStatsList;
    }

    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        return usm;
    }

    public String getLauncherTopApp() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        }

        String result = "";
        //Log.i("MyBackgroundRESULTVer", Build.VERSION.SDK_INT+" - "+Build.VERSION_CODES.LOLLIPOP);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> taskInfoList = manager.getRunningTasks(1);
            //Log.i("MyBackgroundRESULT", "Task"+taskInfoList.get(0).toString());
            if (null != taskInfoList && !taskInfoList.isEmpty()) {
                return taskInfoList.get(0).topActivity.getPackageName();
            }
        } else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 100000;
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
        }

        return result;
    }
}
