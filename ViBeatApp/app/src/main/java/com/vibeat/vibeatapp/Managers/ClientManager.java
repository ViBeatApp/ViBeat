package com.vibeat.vibeatapp.Managers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.vibeat.vibeatapp.AddChange;
import com.vibeat.vibeatapp.DeleteChange;
import com.vibeat.vibeatapp.FBManager;
import com.vibeat.vibeatapp.HelperClasses.SenderThread;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.PlaylistChange;
import com.vibeat.vibeatapp.ServerSide.Command;
import com.vibeat.vibeatapp.ServerSide.partyInfo;
import com.vibeat.vibeatapp.ServerSide.userIntention;
import com.vibeat.vibeatapp.SwapChange;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static com.vibeat.vibeatapp.ServerSide.userIntention.*;

public class ClientManager {

    public User user;
    public Party party;
    public MyApplication app;
    public Location location;
    public partyInfo requested_party = null;
    public boolean waiting_for_response = false;
    public List<PlaylistChange> local_changes;

    public ClientManager(User user, MyApplication app){
        app.semaphore = new Semaphore(0);
        app.semaphoreSender = new Semaphore(0);
        Log.d("Test7", "lock semaphore sender in client manager");
        this.user= user;
        this.party = null;
        this.local_changes = new ArrayList<PlaylistChange>();
        app.sender_thread = new SenderThread(app);

        app.sender_thread.start();
        try {
            app.sender_thread.addCmd(Command.create_authentication_command(user.name, user.id, user.img_path));
            Log.d("Dana", "ClientManager:  add command to sender");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        app.media_manager = new MediaPlayerManager(app);
        app.fb_manager = new FBManager();
        this.app = app;
    }

    public void createParty(){
        user.is_admin = true;
        try {
            if (app.sender_thread != null)
                app.sender_thread.addCmd(Command.create_create_Command(party.party_name,party.is_private));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connectParty(partyInfo party){
        try {
            if (app.sender_thread != null && party != null)
                app.sender_thread.addCmd(Command.create_join_Command(party.id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addTrack(Track track){
        this.party.playlist.addTrack(track);
        PlaylistChange change = new AddChange(track);
        this.local_changes.add(change);
        try {
            if (app.sender_thread != null)
                app.sender_thread.addCmd(Command.create_addSong_Command(track.db_id,change.change_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void nextSong(userIntention userIntent){
        Log.d("GET_READY","next song");
        if(isAdmin()) {
            try {
                app.media_manager.pause();
                int pos = (party.playlist.cur_track + 1) % party.playlist.tracks.size();
                int id = party.playlist.tracks.get(pos).track_id;
                if (app.sender_thread != null) {
                    app.sender_thread.addCmd(Command.create_playSong_Command(id, 0, NEXT_BUTTON));
                    if(party.playlist.is_playing)
                        waiting_for_response = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void swapTrack(int pos1, int pos2){
        if (party.playlist.cur_track == pos1 || party.playlist.cur_track == pos2)
            party.playlist.cur_track = pos1 + pos2 - party.playlist.cur_track;
        try {
            if(pos1 >= 0 && pos1 < party.playlist.tracks.size() &&
                    pos2 >= 0 && pos2 < party.playlist.tracks.size()) {
                int track1_id = party.playlist.tracks.get(pos1).track_id;
                int track2_id = party.playlist.tracks.get(pos2).track_id;
                PlaylistChange change = new SwapChange(track1_id,track2_id);
                this.local_changes.add(change);
                if (app.sender_thread != null)
                    app.sender_thread.addCmd(Command.create_swapSongs_Command(track1_id, track2_id,change.change_id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeTrack(int track_id){
        try {
            int pos = app.client_manager.party.playlist.searchTrack(track_id);
            if(pos == app.client_manager.party.playlist.cur_track)
                app.media_manager.stop();
            PlaylistChange change = new DeleteChange(track_id);
            this.local_changes.add(change);
            if (app.sender_thread != null)
                app.sender_thread.addCmd(Command.create_deleteSong_Command(track_id,change.change_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // not with server
    public Playlist searchTracks(String search_string){
        return new Playlist(app.fb_manager.SearchSongs(search_string),
                false,0);
    }

    public void answerRequest(User requested, boolean answer){
        this.party.changeRequestStatus(requested,answer);
        try {
            if (app.sender_thread != null)
                app.sender_thread.addCmd(Command.create_confirmRequest_Command(requested.id,answer));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void makeAdmin(User connected){
        this.party.makeAdmin(connected);
        try {
            if (app.sender_thread != null)
                app.sender_thread.addCmd(Command.create_makeAdmin_Command(connected.id));
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
                            //Toast.makeText(activity, "Location Changed", Toast.LENGTH_SHORT).show();

                            if( user.is_admin && app.sender_thread != null) {
                                try {
                                    app.sender_thread.addCmd(Command.create_updateLocation_Command(location.getLongitude(),location.getLatitude(),location.getAltitude()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) { }

                        @Override
                        public void onProviderEnabled(String provider) {
                            //Toast.makeText(activity, "Privider Enabled", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            //Toast.makeText(activity, "Privider Disabled", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void getPartiesNearby(){
        try {
            if (app.sender_thread != null) {
                if (location != null)
                    app.sender_thread.addCmd(Command.create_nearbyParties_Command(location.getLongitude(), location.getLatitude(), location.getAltitude()));
                else
                    app.sender_thread.addCmd(Command.create_nearbyParties_Command(0, 0, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void commandPlayPause() {
        Log.d("GET_READY","playpause");
        try {
            if (this.party.playlist.is_playing && app.sender_thread != null) {
                int track_id = this.party.playlist.tracks.get(this.party.playlist.cur_track).track_id;
                app.sender_thread.addCmd(Command.create_playSong_Command(track_id, app.media_manager.getOffset(track_id),PLAY_BUTTON));
                waiting_for_response = true;
            } else {
                int track_id = this.party.playlist.tracks.get(this.party.playlist.cur_track).track_id;
                if (app.sender_thread != null)
                    app.sender_thread.addCmd(Command.create_pause_Command(track_id, app.media_manager.getOffset(track_id)));
            }
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
            if (app.sender_thread != null)
                app.sender_thread.addCmd(Command.create_makePrivate_Command(false));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void turnToPrivate(){
        this.party.is_private = true;
        this.party.request = new ArrayList<User>();

        try {
            if (app.sender_thread != null)
                app.sender_thread.addCmd(Command.create_makePrivate_Command(true));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void leaveParty(){
        try {
            waiting_for_response = false;
            if(party != null && party.playlist!= null) {
                app.media_manager.release_all();
            }
            app.media_manager = new MediaPlayerManager(app);
            if (app.sender_thread != null)
                app.sender_thread.addCmd(Command.create_leaveParty_Command());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void logout(){
        if (app.sender_thread != null)
            app.sender_thread.logout();
    }

    public void changePartyName(String party_name) {
        this.party.party_name = party_name;
        try {
            if (app.sender_thread != null)
                app.sender_thread.addCmd(Command.create_renameParty_Command(party_name));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getURLByTrackId(int track_id) {
        for(Track t : this.party.playlist.tracks)
            if(t.track_id == track_id)
                return t.track_path;
        return "";
    }

    public void sendReady(int track_id) {
        try {
            if (app.sender_thread != null)
                app.sender_thread.addCmd(Command.create_imReady_Command(track_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isAdmin() {
        return (user.is_admin);
    }

    /*public void closeParty() {
        party = null;
        user.is_admin = false;
        terminateConnection();
        startConnection();
    }*/

    public void terminateConnection(boolean fromListener){
        Log.d("Test7", "terminateConnection");
        if(fromListener && app.sender_thread != null) {
            Log.d("Test7", "terminateConnection - try to kill sender");
            synchronized (app.sender_thread.connected) {
                app.sender_thread.connected = false;
            }
            app.sender_thread.interrupt();
            /*try {
                app.sender_thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            Log.d("Test7", "terminateConnection - kill sender");
        }
        else if (app.listener_thread != null) {
            Log.d("Test7", "terminateConnection - try to kill listener");
                synchronized (app.listener_thread.disconnected) {
                    app.listener_thread.disconnected = true;
                }
                app.listener_thread.interrupt();
                /*try {
                    app.listener_thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            Log.d("Test7", "terminateConnection - kill listener");
        }
        this.app.sender_thread = null;
        this.app.listener_thread = null;
    }

    /*public void startConnection(){
        app.sender_thread = new SenderThread(app);
        app.sender_thread.start();
        try {
            app.sender_thread.addCmd(Command.create_authentication_command(user.name, user.id, user.img_path));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public int getTrackPosFromId(int track_id){
        for (int i = 0; i < app.client_manager.party.playlist.tracks.size(); i++){
            if (app.client_manager.party.playlist.tracks.get(i).track_id == track_id)
                return i;
        }
        return -1;
    }

    public void playSongChosen() {
        if(isAdmin()) {
            try {
                app.media_manager.pause();
                int pos = party.playlist.cur_track;
                int id = party.playlist.tracks.get(pos).track_id;
                if (app.sender_thread != null) {
                    app.sender_thread.addCmd(Command.create_playSong_Command(id, 0,NEXT_BUTTON));
                    if(party.playlist.is_playing)
                        waiting_for_response = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
