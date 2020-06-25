package com.example.taskapp.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TaskModel implements Serializable{
    private String title;
    private String description;

    public TaskModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "<<" + title + ">>" + "\n" +
                description + "\n" +
                "________________________";
    }
}