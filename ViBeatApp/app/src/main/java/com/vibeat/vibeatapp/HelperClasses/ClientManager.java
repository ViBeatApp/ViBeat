package com.vibeat.vibeatapp.HelperClasses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.vibeat.vibeatapp.Activities.MainActivity;
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
    public void initLocationTracking(final Activity activity){

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) activity.getApplicationContext()
                    .getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location new_location) {
                            location = new_location;
                            Toast.makeText(activity, "Location Changed", Toast.LENGTH_SHORT).show();

                            if( is_admin )
                                conn.updateAdminLocation(party, location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) { }

                        @Override
                        public void onProviderEnabled(String provider) {
                            Toast.makeText(activity, "Privider Enabled", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            Toast.makeText(activity, "Privider Disabled", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
