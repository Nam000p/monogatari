package com.monogatari.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "MonogatariPrefs";
    private static final String KEY_TOKEN = "access_token";
    private static TokenManager instance;
    private final SharedPreferences sharedPreferences;
    private TokenManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }
    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    public void clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply();
    }
}