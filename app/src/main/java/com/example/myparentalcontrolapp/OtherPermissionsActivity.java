package com.example.myparentalcontrolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myparentalcontrolapp.receivers.LocationReceiver;
import com.example.myparentalcontrolapp.services.GoogleService;
import com.google.type.Color;

public class OtherPermissionsActivity extends AppCompatActivity {
    private Button otherBtn;


    @Override
    protected void onResume() {
        super.onResume();
        if(!hasPermissionNotGranted()) {
            goToNextActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_permissions);

        if(!hasPermissionNotGranted()) {
            goToNextActivity();
        }

        otherBtn = (Button) findViewById(R.id.otherPermBtn);
        otherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!runtime_permissions()) {
                    // start location tracking service
                    goToNextActivity();
                } else {
                    Log.i("MyOtherPermissions", "location permissins");
                    Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                    startService(intent);
                    LocationReceiver locationReceiver = new LocationReceiver();
                    registerReceiver(locationReceiver, new IntentFilter(GoogleService.str_receiver));
                }
            }
        });
    }

    private void goToNextActivity()
    {
        Intent i = new Intent(OtherPermissionsActivity.this, FinishScreenActivity.class);
        startActivity(i);
    }

    private  boolean hasPermissionNotGranted()
    {
        return Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED;
    }

    private boolean runtime_permissions() {
        if(hasPermissionNotGranted()){
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.PACKAGE_USAGE_STATS
            },100);
            return true;
        }
        Log.i("MyOtherPermissions", String.valueOf(ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION)));
        Log.i("MyOtherPermissions", String.valueOf(ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_COARSE_LOCATION)));
        Log.i("MyOtherPermissions", String.valueOf(ContextCompat.checkSelfPermission(this,  Manifest.permission.PACKAGE_USAGE_STATS)));
        Toast.makeText(OtherPermissionsActivity.this, "Your device not supports GPS location", Toast.LENGTH_SHORT).show();
        goToNextActivity();
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                goToNextActivity();
            }else {
                runtime_permissions();
            }
        }
    }

}