package com.vibeat.vibeatapp.HelperClasses;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.vibeat.vibeatapp.Managers.change;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.ServerSide.Command;
import com.vibeat.vibeatapp.ServerSide.CommandClientAux;
import com.vibeat.vibeatapp.ServerSide.ReadWriteAux;
import com.vibeat.vibeatapp.ServerSide.jsonKey;
import com.vibeat.vibeatapp.ServerSide.partyInfo;
import com.vibeat.vibeatapp.ServerSide.trackInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class ListenerThread extends Thread {

    public ReadWriteAux readWriteAux;
    public  MyApplication app;
    public  boolean disconnected = false;
    public boolean openparty = true;

    public ListenerThread(MyApplication app, ReadWriteAux readWriteAux) {
        this.readWriteAux = readWriteAux;
        this.app = app;
    }


    @Override
    public void run() {

        while (!disconnected && openparty) {
            Command cmd = null;
            try {
                Log.e("Listener","before listen");
                cmd = getServerCommand();
                Log.e("Listener","atfer listen");
                handlerCommand(cmd);
            }
            catch (InterruptedException e){
                Log.e("Listener","got Interrupted");
                e.printStackTrace();
                break;
            } catch (JSONException e){
                e.printStackTrace();
                break;
            }
        }
    }

    public Command getServerCommand() throws InterruptedException{

        try {
            return readWriteAux.recieve();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handlerCommand(Command cmd) throws JSONException, InterruptedException {
        if( cmd == null )
            return;

        Log.e("Listener",cmd.cmd_type.name());

        switch (cmd.cmd_type) {

            case SYNC_PARTY:

                int old_cur_track = -1;

                // Getting changes from server. If nothing was changed, get Null.
                JSONArray users = CommandClientAux.getSyncPartyAttribute(cmd, jsonKey.USERS);
                JSONArray requests = CommandClientAux.getSyncPartyAttribute(cmd, jsonKey.REQUESTS);
                JSONArray songs = CommandClientAux.getSyncPartyAttribute(cmd, jsonKey.SONGS);
                JSONArray name = CommandClientAux.getSyncPartyAttribute(cmd, jsonKey.NAME);
                JSONArray is_private = CommandClientAux.getSyncPartyAttribute(cmd, jsonKey.IS_PRIVATE);
                JSONArray cur_track = CommandClientAux.getSyncPartyAttribute(cmd, jsonKey.CURRENT_TRACK_ID);
                boolean move = false;

                //Trying to modify app.client_manager before the client_manager's constructor finish to run.
                app.semaphore.acquire();
                app.semaphore.release();

                if (app.client_manager.party == null) {
                    app.client_manager.party = new Party();
                    move = true;
                }
                synchronized (app.gui_manager.cur_changes) {
                    if (is_private != null) {
                        app.client_manager.party.is_private = is_private.getBoolean(0);
                        app.gui_manager.cur_changes.add(change.is_private);
                    }
                    if (name != null) {
                        app.client_manager.party.party_name = name.getString(0);
                        app.gui_manager.cur_changes.add(change.is_private);
                    }
                    if(users != null){
                        boolean was_admin = app.client_manager.isAdmin();
                        updateUserList(getUserListFromJSON(users), app.client_manager.party);
                        app.gui_manager.cur_changes.add(change.users);
                        if(!was_admin && app.client_manager.isAdmin())
                            app.gui_manager.cur_changes.add(change.admin);
                    }
                    if(requests != null){
                        app.client_manager.party.request = getUserListFromJSON(requests);
                        app.gui_manager.cur_changes.add(change.requests);
                    }
                    if(songs != null){
                        List<Track> new_tracks = getTrackListFromJSON(songs);
                        app.gui_manager.cur_changes.add(change.songs);

                        if (app.client_manager.party.playlist != null) {
                            int prev_size = app.client_manager.party.playlist.tracks.size();
                            app.client_manager.party.playlist.tracks = new_tracks;
                            if (prev_size == 1 && app.client_manager.party.playlist.tracks.size() > 1) {
                                app.media_manager.prepare2nd(app.client_manager.party.playlist.tracks.get(
                                        (app.client_manager.party.playlist.cur_track + 1) %
                                                app.client_manager.party.playlist.tracks.size()).track_id);
                            }

                        } else
                            app.client_manager.party.playlist = new Playlist(new_tracks, false, 0);
                    }
                    if(cur_track != null) {
                        old_cur_track = app.client_manager.party.playlist.cur_track;
                        app.client_manager.party.playlist.cur_track = posFromTrackId(cur_track);
                        app.gui_manager.cur_changes.add(change.cur_track);
                    }
                }
                if (move)
                    app.gui_manager.completeJoin();
                else
                    app.gui_manager.syncParty(old_cur_track);

                break;

            case SEARCH_RESULT:
                JSONArray parties = CommandClientAux.getPartyArray(cmd);
                List<partyInfo> party_list = getPartyListFromJSON(parties);
                app.gui_manager.putPartyResults(party_list);
                break;

            case GET_READY:
                Log.d("Test1","get ready in listener");
                int prep_track_id = cmd.getIntAttribute(jsonKey.TRACK_ID);
                int prep_offset = cmd.getIntAttribute(jsonKey.OFFSET);
                app.media_manager.getReady(prep_track_id, prep_offset);
                break;

            case PLAY_SONG:
                Log.d("Test1","play song in listener");
                app.client_manager.waiting_for_response = false;
                int play_track_id = cmd.getIntAttribute(jsonKey.TRACK_ID);
                int play_offset = cmd.getIntAttribute(jsonKey.OFFSET);
                if (cmd.cmd_info.has(jsonKey.OFFSET_UPDATE_TIME.name())) {
                    long time = Instant.now().getEpochSecond();
                    play_offset += time - cmd.getLongAttribute(jsonKey.OFFSET_UPDATE_TIME) + 40;
                }
                app.media_manager.play(play_track_id,play_offset);
                app.gui_manager.play(play_track_id);
                break;

            case LEAVE_PARTY:
                app.client_manager.party = null;
                app.client_manager.user.is_admin = false;
                break;

            case PAUSE:
                app.gui_manager.pause();
                app.media_manager.pause();
                break;

            case REJECTED:
                app.gui_manager.rejected();
                break;

            case DISCONNECTED:
                if (!disconnected ) {
                    app.gui_manager.disconnected(true);
                    disconnected = true;
                }
                break;

        }
    }


    private List<Track> getTrackListFromJSON(JSONArray arr) throws JSONException{
        List<Track> tracks = new ArrayList<Track>();
        for (int i = 0; i < arr.length(); i++ ){
            trackInfo s = (trackInfo)arr.get(i);
            Track track = app.fb_manager.getTrackByDBid(s.db_id, s.track_id);
            tracks.add(track);
        }
        return tracks;
    }

    private List<User> getUserListFromJSON(JSONArray arr) throws JSONException{
        List<User> users = new ArrayList<User>();
        Log.d("USERS", arr.length()+"");
        for (int i = 0; i < arr.length(); i++ ){
            Log.d("USERS", "in json");
            User u = (User)arr.get(i);
            users.add(u);
        }
        return users;
    }

    private List<partyInfo> getPartyListFromJSON(JSONArray arr) throws JSONException{
        List<partyInfo> parties = new ArrayList<partyInfo>();
        for (int i = 0; i < arr.length(); i++ ){
            partyInfo p = (partyInfo)arr.get(i);
            parties.add(p);
        }
        return parties;
    }
    //also update if the current user is now admin or not.
    private void updateUserList(List<User> users, Party party){
        party.admin.clear();
        party.connected.clear();
        Log.d("USERS", "before for "+users.toString());
        for (User user : users){
            Log.d("USERS", user.name);
            if (user.is_admin) {
                party.admin.add(user);
                if (app.client_manager.user.id == user.id)
                    app.client_manager.user = user;
            }
            else
                party.connected.add(user);
        }
    }

    public int posFromTrackId(JSONArray track_id) {
        try {
            int id = track_id.getInt(0);
            return app.client_manager.getTrackPosFromId(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
