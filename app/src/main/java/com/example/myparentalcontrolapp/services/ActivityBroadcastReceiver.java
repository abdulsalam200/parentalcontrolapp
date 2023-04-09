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
import com.example.myparentalcontrolapp.LoginActivity;
import com.example.myparentalcontrolapp.ScreenBlocker;
import com.example.myparentalcontrolapp.receivers.UnblockReceiver;
import com.example.myparentalcontrolapp.utils.SharedPrefUtils;
import com.example.myparentalcontrolapp.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityBroadcastReceiver extends BroadcastReceiver {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth auth = FirebaseAuth.getInstance();

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
        Utils utils = new Utils(context);
        SharedPrefUtils prefUtil = new SharedPrefUtils(context);
        String packageName = utils.getLauncherTopApp();
        Log.i("ActivityBroadcast", packageName.toString());


        // if app is in blocked apps list, kill the app
        String[] blockedApps = prefUtil.getString("child_blockedApps").split(",");
        if(!packageName.isEmpty() && Arrays.asList(blockedApps).contains(packageName)) {
            Log.i("ActivityBroadcast", "app is blocked");
            blockApp(context, packageName, true);
            return;
        }


        if(!packageName.isEmpty() && packageName.equals("com.android.settings") && auth.getCurrentUser()==null) {
            Intent in = new Intent(context, LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            in.putExtra("login",true);

            context.startActivity(in);
            return;
        }


        // calculate time diff from start
        String appStartTime = prefUtil.getString("startTime");
        String childLimit = prefUtil.getString("child_limit") ;
        long runningAppStartTime = appStartTime.isEmpty() ? 0 : Long.parseLong(appStartTime);
        long timeDiff = (System.currentTimeMillis() - runningAppStartTime)/1000;
        Log.i("ActivityBroadcast", timeDiff+" time difference");

        // if screen time ends, block user to access device
        Boolean isBlocked = prefUtil.getBoolean("userBlocked");
        if(isBlocked) {
            long resetTime = 60 * 4;
            if (timeDiff >= resetTime) {
                Intent i = new Intent(UnblockReceiver.str_receiver);
                context.sendBroadcast(i);
            }
            if (!packageName.equals(context.getPackageName().toString())) {
                blockApp(context, packageName, false);
            }
            return;
        }

        Log.i("MyActivityBroadcast", childLimit);

        // check if screen timeLimit is exceeded
        String runningApp = prefUtil.getString("active_app");
        long timeLimit = (childLimit == null || childLimit.isEmpty()) ? 0 : Long.parseLong(childLimit) * 60;
        if (timeDiff >= timeLimit) {
            prefUtil.putBoolean("userBlocked", true);
            blockApp(context, packageName, false);
            return;
        }

        // if next app opens, log info in database
        if(runningApp.isEmpty() || !runningApp.equals(packageName)){
            prefUtil.putString("active_app", packageName);

            // Get app name from package name
            PackageManager packageManager= context.getPackageManager();
            String appName = packageName;
            try {
                appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Log.e("ActivityBroadcast","AppName Error: "+ e.getMessage());
            } finally {
                if(packageName.isEmpty())
                    return;


                appName = appName.isEmpty() ? packageName : appName;
                // add this app in Activity Logs
                Map<String, Object> data = new HashMap();
                data.put("appName", appName);
                data.put("packageName", packageName);
                data.put("startTime", Long.valueOf(System.currentTimeMillis()));
                data.put("message", appName + " has opened");
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


    }

    private void blockApp(Context context, String packageName, Boolean block) {
        //killThisPackageIfRunning(context, packageName);
        Log.i("ActivityBroadcast", packageName+" closing");
        Intent i = new Intent(context, ScreenBlocker.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("broadcast_receiver", "broadcast_receiver");
        i.putExtra("blockApp", block);
        context.startActivity(i);
    }
}
