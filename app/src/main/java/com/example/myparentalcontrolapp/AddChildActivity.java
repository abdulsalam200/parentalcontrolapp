package com.example.myparentalcontrolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myparentalcontrolapp.models.Child;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddChildActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputEditText nameET,ageET,genderET;
    Button saveChildBtn;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        // redirect to Login Screen if user is not logged in
        if (auth.getCurrentUser() == null)
        {
            Intent intent = new Intent(AddChildActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        nameET = (TextInputEditText) findViewById(R.id.ed3);
        ageET = (TextInputEditText) findViewById(R.id.ed4);
        genderET = (TextInputEditText) findViewById(R.id.ed5);
        saveChildBtn = (Button) findViewById(R.id.btn);



        saveChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameET.getText().toString();
                String age = ageET.getText().toString();
                String gender = genderET.getText().toString();

                if (name.isEmpty())
                {
                    Toast.makeText(AddChildActivity.this, "Name is required.",
                            Toast.LENGTH_SHORT).show();
                            return;
                }

                if (age.isEmpty())
                {
                    Toast.makeText(AddChildActivity.this, "Age is required.",
                            Toast.LENGTH_SHORT).show();
                            return;
                }

                if (gender.isEmpty())
                {
                    Toast.makeText(AddChildActivity.this, "Gender is required.",
                            Toast.LENGTH_SHORT).show();
                            return;
                }

                Map<String, Object> data = new HashMap();
                data.put("name", name);
                data.put("age", age);
                data.put("gender", gender);
                data.put("userId", auth.getCurrentUser().getUid());
                data.put("timeLimit", 5);

                db.collection("childs")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("abd", "DocumentSnapshot written with ID: " + documentReference.getId());

                                Toast.makeText(AddChildActivity.this, "Child created successfully!", Toast.LENGTH_SHORT).show();
                                nameET.setText("");
                                ageET.setText("");
                                genderET.setText("");

                                Intent intent = new Intent(AddChildActivity.this, ChildListActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("abd", "Error adding document", e);
                            }
                        });
            }
        });

    }
}