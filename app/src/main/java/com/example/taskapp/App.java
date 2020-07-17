package com.example.taskapp;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.example.taskapp.room.AppDatabase;

public class App extends MultiDexApplication {

    public static App instance;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this,
                AppDatabase.class, "database")
                .allowMainThreadQueries()
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
