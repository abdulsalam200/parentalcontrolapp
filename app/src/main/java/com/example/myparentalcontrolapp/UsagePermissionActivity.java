package com.example.myparentalcontrolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.myparentalcontrolapp.utils.Utils;

public class UsagePermissionActivity extends AppCompatActivity {

    Button usageBtn;

    private  void checkForUsagePermissionGranted()
    {
        if (!Utils.getUsageStatsList(UsagePermissionActivity.this).isEmpty()){
            Intent intent = new Intent(UsagePermissionActivity.this, OtherPermissionsActivity.class);
            startActivity(intent);
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForUsagePermissionGranted();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_permission);

        checkForUsagePermissionGranted();

        Log.i("abd", "on usage permact");

        usageBtn = (Button) findViewById(R.id.btn2);

        usageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if permission enabled
                if (Utils.getUsageStatsList(UsagePermissionActivity.this).isEmpty()){
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                    return;
                }

                // if already has usage permissions
                Intent intent = new Intent(UsagePermissionActivity.this, OtherPermissionsActivity.class);
                startActivity(intent);
            }
        });
    }
}