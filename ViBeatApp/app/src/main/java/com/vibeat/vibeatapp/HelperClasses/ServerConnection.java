package com.vibeat.vibeatapp.HelperClasses;

import android.location.Location;

import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;

import java.util.List;

enum Answer{
    POSITIVE, NEGATIVE, NO_ANSWER;
}

public class ServerConnection {

    //adds the created party to server DB
    public void addNewParty(Party party){    }

    //connect the user to server
    public void connectToServer(User user){    }

    //update party change to server
    public void updateParty(Party party){    }

    //get updated party from server
    public void syncParty(Party party){    }

    //get answer from server for if we got in or not
    public Answer getRequestAnswer(Party party, User user){ return Answer.NO_ANSWER; }

    //send answer for request to join he party
    public void sendRequestAnswer(Party party, User requested, boolean ans){    }

    //update the saved location of he party admin
    public void updateAdminLocation(Party party, Location new_loc){     }

    //return from server the parties near the location
    public List<Party> getPartiesByLocation(Location location){ return null; }

    //return from server search results from tracks
    public List<Track> getTracksByString(String search_string){ return null; }
}
