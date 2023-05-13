package com.example.myparentalcontrolapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myparentalcontrolapp.utils.SharedPrefUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.w3c.dom.Document;

public class UnblockReceiver extends BroadcastReceiver {

    SharedPrefUtils prefUtil;

    public  static String str_receiver = "com.example.myparentalcontrolapp.unblock";

    @Override
    public void onReceive(Context context, Intent intent) {
        prefUtil = new SharedPrefUtils(context);

        String childId = intent.getStringExtra("childId");
        String action = intent.getStringExtra("actionName");
        Log.i("myunblockreceiver", "onReceive: "+childId.equals(prefUtil.getString("child_id")));



        if (childId.equals(prefUtil.getString("child_id"))) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Log.i("myunblockreceiver", "ChildonReceive: "+childId);

            DocumentReference docRef = db.collection("childs").document(childId);
            docRef.get(Source.SERVER)
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {
                            if (doc.exists()) {
                                Log.i("myunblockreceiver", doc.getId());
                                switch (action) {
                                    case "reset":
                                        Log.i("myunblockreceiver", "onReceive: reset timer");
                                        prefUtil.putBoolean("userBlocked", false);
                                        prefUtil.putString("child_limit", String.valueOf(doc.getLong("timeLimit")));
                                        prefUtil.putString("startTime", String.valueOf(System.currentTimeMillis()));
                                        break;
                                    case "block-apps":
                                        Log.i("myunblockreceiver", "onReceive: change blocked apps"+doc.getString("blockedApps"));
                                        prefUtil.putString("child_blockedApps", doc.getString("blockedApps"));
                                        break;
                                }
                            } else {
                                // Document does not exist
                                Log.i("myunblockreceiver", "doc does not exists");
                            }
                        }
                    });

        }

    }
}