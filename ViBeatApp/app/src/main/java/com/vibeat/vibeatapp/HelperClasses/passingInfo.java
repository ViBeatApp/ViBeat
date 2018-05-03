package com.vibeat.vibeatapp.HelperClasses;

import com.vibeat.vibeatapp.Objects.Playlist;

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

    public static class Party{
        com.vibeat.vibeatapp.User[] admin;
        com.vibeat.vibeatapp.User[] connected;
        com.vibeat.vibeatapp.User[] request;
        Playlist playlist;
    }
}
