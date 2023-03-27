package com.example.myparentalcontrolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myparentalcontrolapp.utils.SharedPrefUtils;

public class ScreenBlocker extends AppCompatActivity {

    SharedPrefUtils prefUtil;

    @Override
    protected void onResume() {
        super.onResume();
        checkForUnblock();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_blocker);
        prefUtil = new SharedPrefUtils(ScreenBlocker.this);
        checkForUnblock();
    }
        private void checkForUnblock(){
            Boolean isBlocked = prefUtil.getBoolean("userBlocked");
            if (!isBlocked) {
                Intent i = new Intent(ScreenBlocker.this, DashboardActivity.class);
                startActivity(i);
                return;
            }
        }
}