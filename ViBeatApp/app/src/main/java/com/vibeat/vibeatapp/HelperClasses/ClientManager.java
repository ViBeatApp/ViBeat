package com.vibeat.vibeatapp.HelperClasses;

import android.content.Context;
import android.location.Location;

import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;

import java.util.ArrayList;
import java.util.List;

public class ClientManager {

    public User user;
    public Party party;
    public boolean is_admin;

    public ServerConnection conn;
    public Location location;

    public ClientManager(User user){
        this.user= user;
        this.party = null;
        this.is_admin = false;

        conn = new ServerConnection();
        conn.connectToServer(this.user);
    }

    public void createParty(){
        is_admin = true;
        conn.addNewParty(this.party);
    }

    public boolean connectParty(){
        if (party.is_private) {
            party.addRequest(user);
            conn.updateParty(this.party);

            boolean user_canceled = false; // FIND IF USER PRESSED CANCEL
            while (true){
                switch (conn.getRequestAnswer(party,user)){
                    case POSITIVE:
                        conn.syncParty(this.party);
                        return true;
                    case NEGATIVE:
                        return false;
                    case NO_ANSWER:
                        if (user_canceled)
                            return false;
                }
            }
        }
        else{
            this.party = party;
            conn.syncParty(this.party);
            this.party.addConnected(user);
            conn.updateParty(this.party);
        }
        this.is_admin = false;
        return true;
    }


    public void addTrack(Track track){
        this.party.playlist.addTrack(track);
        conn.updateParty(this.party);
    }

    // get track item and change the current track index to track's index.
    public void changeTrack(Track track){
        int pos = this.party.playlist.tracks.indexOf(track);
        this.party.playlist.cur_track = pos;
        conn.sendTrackCommand(this.party, pos);
    }

    public Playlist searchTracks(String search_string){
        return new Playlist(conn.getTracksByString(search_string),
                false,0);
    }


    public void answerRequest(User requested, boolean answer){
        this.party.changeRequestStatus(requested,answer);
        conn.updateParty(this.party);
        conn.sendRequestAnswer(this.party, requested, answer);
    }

    public void makeAdmin(User connected){
        this.party.makeAdmin(connected);
        conn.updateParty(this.party);
    }


    //update self location and ping to server
    public void updateLocation(){
        //UPDATE THE LOCATION
        if( is_admin )
            conn.updateAdminLocation(this.party, this.location);
    }

    public List<Party> getPartiesNearby(){
        return conn.getPartiesByLocation(this.location);
    }


    public void commandPlayPause(){
        this.party.playlist.is_playing = !this.party.playlist.is_playing;
        conn.sendPlayPauseCommand(this.party);
    }

    public void turnToPublic(){
        this.party.is_private = false;
        this.party.connected.addAll(this.party.request);
        for (User u : this.party.request){
            conn.sendRequestAnswer(this.party, u, true);
        }
        this.party.request.clear();
        conn.updateParty(this.party);
    }

    public void turnToPrivate(){
        this.party.is_private = true;
        this.party.request = new ArrayList<User>();
        conn.updateParty(this.party);
    }
}
