package com.jschiefner.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    public static final String SHARED_PREF_NAME = "shared_pref_name";
    public static final String KEY_ACCESS_TOKEN = "token";
    private static Context context;
    private static PreferenceManager instance;

    private PreferenceManager(Context context) {
        this.context = context;
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    public boolean storeToken(String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
        return true;
    }

    public String getToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }
}
