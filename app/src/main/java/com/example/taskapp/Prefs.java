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

    public void permissionGranted(boolean value) {
        sharedPreferences
                .edit()
                .putBoolean("permissionGranted", value)
                .apply();
    }

    public boolean permissionGranted(){
        return sharedPreferences.getBoolean("permissionGranted", false);
    }


    public void name(String value) {
        sharedPreferences
                .edit()
                .putString("name", value)
                .apply();
    }

    public String name(){
        return sharedPreferences.getString("name", "DefValue");
    }

    public void avatarUrl(String value) {
        sharedPreferences
                .edit()
                .putString("avatarUrl", value)
                .apply();
    }

    public String avatarUrl(){
        return sharedPreferences.getString("avatarUrl", "");
    }
    public void desc(String value) {
        sharedPreferences
                .edit()
                .putString("desc", value)
                .apply();
    }

    public String desc(){
        return sharedPreferences.getString("desc", "");
    }





    void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
