package com.vibeat.vibeatapp.HelperClasses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.vibeat.vibeatapp.Command;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;

import org.json.JSONException;

import java.util.ArrayList;

public class ClientManager {

    public User user;
    public Party party;
    public boolean is_admin;
    public MyApplication app;
    public Location location;
    public SenderThread senderThread;

    public ClientManager(User user, MyApplication app){
        this.user= user;
        this.party = null;
        this.is_admin = false;
        this.senderThread = new SenderThread();

        senderThread.start();
        try {
            senderThread.addCmd(Command.get_authentication_command(user.name, user.id, user.img_path.getBytes()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.app = app;
    }

    public void createParty(){
        is_admin = true;
        try {
            senderThread.addCmd(Command.get_create_Command(party.party_name,party.is_private));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connectParty(){
        /*if (party.is_private) {
            party.addRequest(user);
            conn.updateParty(this.party);
        }
        else{
            this.party = party;
            conn.syncParty(this.party);
            this.party.addConnected(user);
            conn.updateParty(this.party);
        }
        this.is_admin = false;
        return true;*/
        try {
            senderThread.addCmd(Command.get_join_Command(party.id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addTrack(Track track){
        this.party.playlist.addTrack(track);
        try {
            senderThread.addCmd(Command.get_addSong_Command(track.track_path));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void nextSong(){
        try {
            senderThread.addCmd(Command.get_playSong_Command(party.playlist.cur_track+1, 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void swapTrack(int pos1, int pos2){
        try {
            senderThread.addCmd(Command.get_swapSongs_Command(pos1,pos2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // not with server
    public Playlist searchTracks(String search_string){
        return new Playlist(DBManager.getTracksByString(search_string),
                false,0);
    }

    public void answerRequest(User requested, boolean answer){
        this.party.changeRequestStatus(requested,answer);
        try {
            senderThread.addCmd(Command.get_confirmRequest_Command(requested.id,answer));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void makeAdmin(User connected){
        this.party.makeAdmin(connected);
        try {
            senderThread.addCmd(Command.get_makeAdmin_Command(connected.id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //CHANGE THE VALUE SENT TO SERVER
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

                            if( is_admin ) {
                                try {
                                    senderThread.addCmd(Command.get_updateLocation_Command(0));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
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

    public void getPartiesNearby() throws InterruptedException {
        try {
            senderThread.addCmd(Command.get_nearbyParties_Command(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void commandPlayPause(){
        //conn.sendPlayPauseCommand(this.party);
        /*if (!this.party.playlist.is_playing) {
            try {
                app.media_manager.play();
            }
            catch(IOException e){

            }
        }
        else
            app.media_manager.pause();*/
        this.party.playlist.is_playing = !this.party.playlist.is_playing;

        try {
            senderThread.addCmd(Command.get_playSong_Command(this.party.playlist.cur_track,app.media_manager.getOffset()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void turnToPublic(){
        this.party.is_private = false;
        this.party.connected.addAll(this.party.request);
        for (User u : this.party.request){
            //in the server : conn.sendRequestAnswer(this.party, u, true);
        }
        this.party.request.clear();
        //in the server : conn.updateParty(this.party);

        try {
            senderThread.addCmd(Command.get_makePrivate_Command(false));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void turnToPrivate(){
        this.party.is_private = true;
        this.party.request = new ArrayList<User>();

        try {
            senderThread.addCmd(Command.get_makePrivate_Command(true));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //WHY LOCATION?
    public void leaveParty(){
        try {
            senderThread.addCmd(Command.get_leaveParty_Command(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.party = null;
    }

    public void logout(){
        senderThread.logout();
    }
}
