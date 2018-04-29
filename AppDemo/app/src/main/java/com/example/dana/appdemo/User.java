package com.example.dana.appdemo;

public class User {

    public String name;
    public int icon_id;
    public int id;
    public boolean is_admin;


    public User(String name, int icon_id, int id, boolean is_admin) {
        this.name = name;
        this.icon_id = icon_id;
        this.id = id;
        this.is_admin = is_admin;
    }

    public String getName() {
        return name;
    }

    public int getIcon_id() {
        return icon_id;
    }

    public int getId() {
        return id;
    }
}
