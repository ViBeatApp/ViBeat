package com.example.dana.appdemo;

import java.io.Serializable;

public class passingInfo implements Serializable {
    public int user_id;
    public String user_name;

    public int group_id;
    public int chosen;

    public passingInfo(int user_id, String user_name, int group_id, int chosen) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.group_id = group_id;
        this.chosen = chosen;
    }
}
