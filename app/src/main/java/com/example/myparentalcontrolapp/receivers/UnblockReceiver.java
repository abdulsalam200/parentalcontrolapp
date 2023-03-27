package com.example.myparentalcontrolapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.myparentalcontrolapp.utils.SharedPrefUtils;

public class UnblockReceiver extends BroadcastReceiver {

    SharedPrefUtils prefUtil;

    public  static String str_receiver = "parentalControlApp.unblock.intent";

    @Override
    public void onReceive(Context context, Intent intent) {
        prefUtil = new SharedPrefUtils(context);
        Log.i("myunblockreceiver", "onReceive: ");
        prefUtil.putBoolean("userBlocked", false);
        prefUtil.putString("startTime", String.valueOf(System.currentTimeMillis()));

    }
}