package com.vibeat.vibeatapp.Objects;

import android.location.Location;

public class User {

    public String name;
    public String img_path;
    public int id;

    public User(String name, String path, int id) {
        this.name = name;
        this.img_path = path;
        this.id = id;
    }

}
