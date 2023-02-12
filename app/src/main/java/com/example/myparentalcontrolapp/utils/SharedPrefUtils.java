package com.example.myparentalcontrolapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {
    private static final String SHARED_APP_PREFERENCE_NAME = "SharedPref";
    private final SharedPreferences pref;
    private SharedPreferences.Editor mEditor;

    public SharedPrefUtils(Context context) {
        this.pref = context.getSharedPreferences(SHARED_APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefUtils getInstance(Context context) {
        return new SharedPrefUtils(context);
    }


    public void putString(String key, String value) {
        pref.edit().putString(key, value).apply();
    }

    public void putInteger(String key, int value) {
        pref.edit().putInt(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        pref.edit().putBoolean(key, value).apply();
    }

    public String getString(String key) {
        return pref.getString(key, "");
    }

    public int getInteger(String key) {
        return pref.getInt(key, 0);
    }

    public boolean getBoolean(String key) {
        return pref.getBoolean(key, false);
    }


}
