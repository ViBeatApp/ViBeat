package com.vibeat.vibeatapp.Objects;

import java.util.ArrayList;
import java.util.List;

public class Party {

    public Playlist playlist;

    public List<User> admin = new ArrayList<User>(); //creator is te first admin
    public List<User> connected = new ArrayList<User>();
    public List<User> request = new ArrayList<User>();

    public String party_name;
    public boolean is_private;

    public Party(User creator, String party_name, boolean is_private){
        this.admin.add(creator);
        this.party_name = party_name;
        this.is_private = is_private;
        this.playlist = new Playlist();
    }

    public void addConnected(User user){
        this.connected.add(user);
    }

    public void addRequest(User user){
        this.request.add(user);
    }

    public void makeAdmin(User user){
        admin.add(user);
        connected.remove(user);
    }

    public void changeRequestStatus(User requested, boolean answer){
        request.remove(requested);
        if( answer )
            connected.add(requested);
    }

    public User getCreator(){
        return admin.get(0);
    }

}
