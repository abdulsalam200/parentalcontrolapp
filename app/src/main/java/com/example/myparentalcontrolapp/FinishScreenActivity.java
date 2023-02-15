package com.example.myparentalcontrolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myparentalcontrolapp.services.BackgroundManager;
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


        prefUtil = new SharedPrefUtils(FinishScreenActivity.this);
        finishBtn = (Button) findViewById(R.id.btn4);


        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prefUtil.putString("startTime", String.valueOf(System.currentTimeMillis()));

                auth.signOut();
                BackgroundManager.getInstance().init(FinishScreenActivity.this).startService();
//                FinishScreenActivity.this.finishAffinity();
                moveTaskToBack(true);

//                finishAndRemoveTask();
//                System.exit(0);
            }
        });
    }
}