package com.example.myparentalcontrolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myparentalcontrolapp.receivers.LocationReceiver;
import com.example.myparentalcontrolapp.receivers.MyDeviceAdminReceiver;
import com.example.myparentalcontrolapp.receivers.UnblockReceiver;
import com.example.myparentalcontrolapp.receivers.UninstallReceiver;
import com.example.myparentalcontrolapp.services.BackgroundManager;
import com.example.myparentalcontrolapp.services.GoogleService;
import com.example.myparentalcontrolapp.utils.SharedPrefUtils;
import com.google.firebase.auth.FirebaseAuth;

public class FinishScreenActivity extends AppCompatActivity {
    SharedPrefUtils prefUtil;
    Button finishBtn;
    FirebaseAuth auth;

    @Override
    protected void onResume() {
        super.onResume();

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(FinishScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        CheckForDeviceAdmin();
    }

    private  void CheckForDeviceAdmin()
    {
        finishBtn = (Button) findViewById(R.id.btn4);
        finishBtn.setEnabled(true);
        return;
//        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//        ComponentName componentName = new ComponentName(FinishScreenActivity.this, MyDeviceAdminReceiver.class);
//        if (devicePolicyManager.isAdminActive(componentName)) {
//            Log.i("MyUninstallReceiver", "Finish device admin enabled");
//            finishBtn = (Button) findViewById(R.id.btn4);
//            finishBtn.setEnabled(true);
//            return;
//        }
//        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
//        intent.putExtra(DevicePolicyManager.DELEGATION_BLOCK_UNINSTALL, componentName);
//        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_screen);

        auth = FirebaseAuth.getInstance();


        prefUtil = new SharedPrefUtils(FinishScreenActivity.this);
        finishBtn = (Button) findViewById(R.id.btn4);
        finishBtn.setEnabled(false);

        CheckForDeviceAdmin();

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefUtil.putString("startTime", String.valueOf(System.currentTimeMillis()));
                auth.signOut();

                // uninstall receiver
//                UninstallReceiver uninstallReceiver = new UninstallReceiver();
//                registerReceiver(uninstallReceiver, new IntentFilter(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));


                // start location tracking service
                Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                startService(intent);
                LocationReceiver locationReceiver = new LocationReceiver();
                registerReceiver(locationReceiver, new IntentFilter(GoogleService.str_receiver));

                UnblockReceiver unblockReceiver = new UnblockReceiver();
                registerReceiver(unblockReceiver, new IntentFilter(UnblockReceiver.str_receiver));

                // start background service for usage monitor
                BackgroundManager.getInstance().init(FinishScreenActivity.this).startService();

                moveTaskToBack(true);
            }
        });
    }
}