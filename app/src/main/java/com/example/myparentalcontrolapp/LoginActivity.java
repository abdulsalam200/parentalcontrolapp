package com.example.myparentalcontrolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myparentalcontrolapp.utils.SharedPrefUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    SharedPrefUtils prefUtil;

    EditText emailEditText, passwordEditText;
    Button loginBtn;

    @Override
    protected void onResume() {
        super.onResume();
        Boolean isSettingsApp = getIntent().getBooleanExtra("login", false);

        // if user is already logged in
        if(auth.getCurrentUser()!= null) {
            openChildList();
        }

        // if timer started and settings app is not requested
        if (prefUtil.getString("startTime") != "" && !isSettingsApp){
            Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        auth = FirebaseAuth.getInstance();
        prefUtil = new SharedPrefUtils(LoginActivity.this);
        Boolean isSettingsApp = getIntent().getBooleanExtra("login", false);


        // if user is already logged in
        if(auth.getCurrentUser()!= null) {
            openChildList();
        }

        // if timer started and settings app is not requested
        if (prefUtil.getString("startTime") != "" && !isSettingsApp){
            Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
            startActivity(i);
        }

        loginBtn = (Button) findViewById(R.id.btn1);
        emailEditText = (EditText) findViewById(R.id.ed1);
        passwordEditText = (EditText) findViewById(R.id.ed2);

        // eventListener on login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get email and password values from text fields
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Email/Password required.",
                            Toast.LENGTH_SHORT).show();
                            return;
                }
                // call login method with email and password
                login(email,password, isSettingsApp);
            }
        });
    }

    private  void login(String email, String password, Boolean isSettingsApp)
    {
        RelativeLayout loading = (RelativeLayout)findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FirebaseTest", "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();

                            // Start Next Activity
                            if (user != null) {
                                loading.setVisibility(View.INVISIBLE);
                                if(isSettingsApp) {
                                    moveTaskToBack(false);
                                } else {
                                    openChildList();
                                }

                            }
                        } else {
                            loading.setVisibility(View.INVISIBLE);
                            // If sign in fails, display a message to the user.
                            Log.w("FirebaseTest", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void openChildList() {
        Intent intent = new Intent(this, ChildListActivity.class);
        startActivity(intent);
    }

}