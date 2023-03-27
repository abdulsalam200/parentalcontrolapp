package com.example.myparentalcontrolapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.myparentalcontrolapp.utils.SharedPrefUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;

public class LocationReceiver extends BroadcastReceiver {

    SharedPrefUtils prefUtils;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("GoogleService", "location received");
        prefUtils = new SharedPrefUtils(context);
        String childId = prefUtils.getString("child_id");
        Log.i("GoogleService", childId);


        Map<String, Object> data = new HashMap();
        data.put("locLatitude", intent.getStringExtra("latitude"));
        data.put("locLongitude", intent.getStringExtra("longitude"));
        DocumentReference doc = db.collection("childs").document(childId);
        doc.update("locLatitude", intent.getStringExtra("latitude"));
        doc.update("locLongitude", intent.getStringExtra("longitude"));

    }
}