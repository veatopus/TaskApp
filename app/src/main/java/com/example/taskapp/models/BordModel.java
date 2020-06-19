package com.example.taskapp.models;

public class BordModel {
    private int image;
    private String title;
    private String description;

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BordModel(int image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }
}
