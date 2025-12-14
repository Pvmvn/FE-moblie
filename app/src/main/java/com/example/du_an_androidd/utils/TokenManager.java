package com.example.du_an_androidd.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences("LIBRARY_APP_PREFS", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveToken(String token) {
        editor.putString("ACCESS_TOKEN", token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString("ACCESS_TOKEN", null);
    }

    public void clearToken() {
        editor.remove("ACCESS_TOKEN");
        editor.apply();
    }
}