package com.vibeat.vibeatapp.HelperClasses;

import android.content.Context;
import android.location.Location;

import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;

import java.util.List;

public class ClientManager {

    User user;
    Party party;
    boolean is_admin;

    ServerConnection conn;
    Location location;

    public ClientManager(User user){
        this.user= user;
        this.party = null;
        this.is_admin = false;

        conn = new ServerConnection();
        conn.connectToServer(this.user);
    }

    public void createParty(String party_name, boolean is_private){
        is_admin = true;
        this.party = new Party(user,party_name,is_private);
        conn.addNewParty(this.party);
    }

    public boolean connectParty(Party party){
        if (party.is_private) {
            this.party.addRequest(user);
            conn.updateParty(this.party);

            boolean user_canceled = false; // FIND IF USER PRESSED CANCEL
            while (true){
                switch (conn.getRequestAnswer(party,user)){
                    case POSITIVE:
                        this.party = party;
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

    public List<Track> searchTracks(String search_string){
        return conn.getTracksByString(search_string);
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
        conn.updateParty(this.party);
    }
}
