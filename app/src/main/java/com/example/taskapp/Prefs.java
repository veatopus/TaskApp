package com.example.taskapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences sharedPreferences;

    public Prefs(Activity activity) {
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public void isShown(boolean value) {
        sharedPreferences
                .edit()
                .putBoolean("isShown", value)
                .apply();
    }

    boolean isShown(){
        return sharedPreferences.getBoolean("isShown", false);
    }

    void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
