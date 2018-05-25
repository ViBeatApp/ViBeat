package com.vibeat.vibeatapp.Objects;

public class User {

    public String name;
    public String img_path;
    public int id;
    public boolean is_admin;

    public User(String name, String path, int id, boolean is_admin) {
        this.name = name;
        this.img_path = path;
        this.id = id;
        this.is_admin = is_admin;
    }

}
