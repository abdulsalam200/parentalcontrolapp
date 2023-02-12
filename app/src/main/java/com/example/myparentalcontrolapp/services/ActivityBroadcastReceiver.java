package com.example.myparentalcontrolapp.services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.myparentalcontrolapp.AddChildActivity;
import com.example.myparentalcontrolapp.ChildListActivity;
import com.example.myparentalcontrolapp.utils.SharedPrefUtils;
import com.example.myparentalcontrolapp.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityBroadcastReceiver extends BroadcastReceiver {
    Calendar currentTime;
    Calendar fromTime;
    Calendar toTime;
    Calendar currentDay;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void killThisPackageIfRunning(final Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(startMain);
        activityManager.killBackgroundProcesses(packageName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        // lock the app after given time
        Log.i("MyBackgroundReceive", context.getPackageName().toString());

        Utils utils = new Utils(context);
        SharedPrefUtils prefUtil = new SharedPrefUtils(context);

        // get app package name using UsageEvents query
        String packageName = utils.getLauncherTopApp();

        int appTimeLimit = 1; // 1 minute

        String runningApp = prefUtil.getString("active_app");
        String appStartTime = prefUtil.getString("active_app_start");
        long runningAppStartTime = appStartTime.isEmpty() ? 0 : Long.parseLong(appStartTime);

//        Log.i("MyTimerTime", (new Date(runningAppStartTime)).toString());
        if(!runningApp.isEmpty() && runningApp.equals(packageName)) {
            long timeDiff = (System.currentTimeMillis() - runningAppStartTime)/1000;
//            Log.i("MyTimerDiff", String.valueOf(timeDiff));
            if (timeDiff >= (appTimeLimit * 60)) {
//                Log.i("MyTimer", "Lock the app");
                killThisPackageIfRunning(context, packageName);
//                Intent i = new Intent(context, ScreenBlocker.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                i.putExtra("broadcast_receiver", "broadcast_receiver");
//                context.startActivity(i);
            }
        } else {
            prefUtil.putString("active_app", packageName);
            prefUtil.putString("active_app_start", String.valueOf(System.currentTimeMillis()));

            // Get app name from package name
            PackageManager packageManager= context.getPackageManager();
            String appName = null;
            try {
                appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
//                Log.e("MyTimer", e.getMessage());
            }

            // add this app in Activity Logs
            Map<String, Object> data = new HashMap();
            data.put("appName", appName);
            data.put("packageName", packageName);
            data.put("startTime", Long.valueOf(System.currentTimeMillis()));
            data.put("message", appName+" has opened");
            data.put("childId", prefUtil.getString("child_id"));

            db.collection("activity_logs")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("abd", "Activity Log written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("abd", "Error adding Activity Log", e);
                        }
                    });


        }


    }

    public boolean checkTime(String startTimeHour, String startTimeMin, String endTimeHour, String endTimeMin) {
        try {
            currentTime = Calendar.getInstance();
            currentTime.get(Calendar.HOUR_OF_DAY);
            currentTime.get(Calendar.MINUTE);
            fromTime = Calendar.getInstance();
            fromTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTimeHour));
            fromTime.set(Calendar.MINUTE, Integer.valueOf(startTimeMin));
            fromTime.set(Calendar.SECOND, 0);
            fromTime.set(Calendar.MILLISECOND, 0);
            toTime = Calendar.getInstance();
            toTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(endTimeHour));
            toTime.set(Calendar.MINUTE, Integer.valueOf(endTimeMin));
            toTime.set(Calendar.SECOND, 0);
            toTime.set(Calendar.MILLISECOND, 0);
            if(currentTime.after(fromTime) && currentTime.before(toTime)){
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean checkDay(List<String> weekdays){
        currentDay = Calendar.getInstance();
        String today = LocalDate.now().getDayOfWeek().name();
        if(weekdays.contains(today)){
            return true;
        } else {
            return false;
        }
    }
}
