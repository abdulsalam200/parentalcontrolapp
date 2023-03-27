package com.example.myparentalcontrolapp.receivers;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myparentalcontrolapp.FinishScreenActivity;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        Log.i("MyUninstallReceiver", "device admin enabled");
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

    }


    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Log.i("MyUninstallReceiver", intent.getDataString());
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (packageName.equals(context.getPackageName())) {
                Log.i("MyUninstallReceiver", "This app is uninstalling");
                // The app is being uninstalled, do something here
            }
        }
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        Log.i("MyUninstallReceiver", "device admin disabled");
    }
}
