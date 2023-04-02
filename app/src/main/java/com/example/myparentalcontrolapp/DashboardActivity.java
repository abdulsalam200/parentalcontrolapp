package com.example.myparentalcontrolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.example.myparentalcontrolapp.utils.SharedPrefUtils;

public class DashboardActivity extends AppCompatActivity {

    TextView textView;
    SharedPrefUtils prefUtil;

    private  void updateTimer()
    {
        Boolean isBlocked = prefUtil.getBoolean("userBlocked");
        if (isBlocked) {
            Intent i = new Intent(DashboardActivity.this, ScreenBlocker.class);
            startActivity(i);
            return;
        }

        String appStartTime = prefUtil.getString("startTime");
        String childLimit = prefUtil.getString("child_limit");
        Log.i("DashboardActivity", childLimit);
        long runningAppStartTime = (appStartTime == null || appStartTime.isEmpty()) ? 0 : Long.parseLong(appStartTime);
        long timeDiff = (System.currentTimeMillis() - runningAppStartTime)/1000;
        long timeLimit = (childLimit == null || childLimit.isEmpty()) ? 0 : Long.parseLong(childLimit) * 60;
        long remainingTime = timeLimit - timeDiff;
        new CountDownTimer(remainingTime * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                Long sec = millisUntilFinished / 1000;
                Long mins = sec >= 60 ? sec/60 : 0;
                sec = sec%60;
                Long hrs = mins >= 60 ? mins/60 : 0;
                mins = mins%60;

                String hours = hrs/10 <1 ? "0"+hrs : String.valueOf(hrs);
                String minutes = mins/10 < 1 ? "0"+mins : String.valueOf(mins);
                String seconds = sec/10 < 1 ? "0"+sec : String.valueOf(sec);
                textView.setText(hours+":"+minutes+":"+seconds);
            }

            public void onFinish() {
                textView.setText("00:00");
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTimer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        textView = (TextView)findViewById(R.id.txt1);
        textView.setText("00:00");
        prefUtil = new SharedPrefUtils(DashboardActivity.this);

        updateTimer();



    }
}