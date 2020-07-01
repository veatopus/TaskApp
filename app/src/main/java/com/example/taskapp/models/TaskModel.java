package com.example.taskapp.models;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class TaskModel implements Serializable, Comparable<TaskModel>{

    @PrimaryKey(autoGenerate = true)
    private long id;
    private int color = Color.WHITE;
    private String title;
    private String description;

    public TaskModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @NonNull
    @Override
    public String toString() {
        return
                "<<" + title + ">>" + "\n" +
                description + "\n" +
                "________________________";
    }

    @Override
    public int compareTo(TaskModel o) {
        return title.compareTo(o.getTitle());
    }
}