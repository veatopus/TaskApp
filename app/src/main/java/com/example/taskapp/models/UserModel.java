package com.example.taskapp.models;

public class UserModel {
    private String name;
    private String desc;
    private String avatar;


    public UserModel() {

    }

    public UserModel(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
