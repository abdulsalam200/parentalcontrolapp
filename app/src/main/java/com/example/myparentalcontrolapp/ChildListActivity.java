package com.example.myparentalcontrolapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.myparentalcontrolapp.adapters.ChildListAdapter;
import com.example.myparentalcontrolapp.models.Child;
import com.example.myparentalcontrolapp.utils.SharedPrefUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChildListActivity extends AppCompatActivity {

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    SharedPrefUtils prefs;

    ArrayList<Child> childList = new ArrayList<Child>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_list_activity);


        prefs = new SharedPrefUtils(ChildListActivity.this);

        if (currentUser == null)
        {
            Intent intent = new Intent(ChildListActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        ListView listView = (ListView) findViewById(R.id.listview);
        Button addChildbtn = (Button) findViewById(R.id.btn5);

        ChildListAdapter adapter = new ChildListAdapter(ChildListActivity.this,R.layout.activity_listview, childList);


        String userId = currentUser.getUid();
        database.collection("childs").whereEqualTo("userId", userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot records, @Nullable FirebaseFirestoreException error) {
                childList.clear();
                if (records != null) {
                    for (DocumentSnapshot document : records) {
                        String name = document.getString("name");
                        String age = document.getString("age");
                        String gender = document.getString("gender");
                        String id = document.getId();
                        Long timeLimit = document.getLong("timeLimit");
                        String blockedApps = document.getString("blockedApps");

                        childList.add(new Child(name, age, gender, id, timeLimit, blockedApps));
                    }
                }
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Child selectedChild = childList.get(i);
                        Log.i("ChildListActivity", "selected - " + (selectedChild.getTimeLimit() == null));

                        Long childLimit = selectedChild.getTimeLimit();
                        prefs.putString("child_id", selectedChild.getId());
                        prefs.putString("child_limit", childLimit == null ? null : String.valueOf(childLimit));
                        prefs.putString("child_blockedApps", selectedChild.getBlockedApps());

                        Intent intent = new Intent(ChildListActivity.this, UsagePermissionActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });



      addChildbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              openAddChildActivity();
          }
      });
    }

    public void openAddChildActivity() {
        Intent intent = new Intent(this, AddChildActivity.class);
        startActivity(intent);
    }


}