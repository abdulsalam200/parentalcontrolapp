package com.example.myparentalcontrolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myparentalcontrolapp.receivers.UnblockReceiver;
import com.example.myparentalcontrolapp.services.MyForegroundService;
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

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_screen);

        auth = FirebaseAuth.getInstance();


        Toast.makeText(FinishScreenActivity.this, getPackageName(), Toast.LENGTH_SHORT);


        prefUtil = new SharedPrefUtils(FinishScreenActivity.this);
        finishBtn = (Button) findViewById(R.id.btn4);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefUtil.putString("startTime", String.valueOf(System.currentTimeMillis()));
                auth.signOut();

                UnblockReceiver unblockReceiver = new UnblockReceiver();
                registerReceiver(unblockReceiver, new IntentFilter(UnblockReceiver.str_receiver));

                // start background service for usage monitor
               // BackgroundManager.getInstance().init(FinishScreenActivity.this).startService();

                Intent intent = new Intent(FinishScreenActivity.this, MyForegroundService.class);
                startService(intent);

                moveTaskToBack(true);
            }
        });
    }
}