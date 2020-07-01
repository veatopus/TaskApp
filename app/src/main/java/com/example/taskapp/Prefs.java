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

    public void isSortingAlphabetically(boolean value) {
        sharedPreferences
                .edit()
                .putBoolean("isSortingAlphabetically", value)
                .apply();
    }

    public boolean isSortingAlphabetically(){
        return sharedPreferences.getBoolean("isSortingAlphabetically", false);
    }



    void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
