package com.vibeat.vibeatapp.HelperClasses;

import android.util.Log;

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

    public void handlerCommand(Command cmd) throws  JSONException{
        if( cmd == null )
            return;

        Log.e("Listener",cmd.cmd_type.name());

        switch (cmd.cmd_type){

            /*
            * Get from the server the entire party.
            * things that can updated:
            * 1. the current user is now an admin.
            * 2. the playlist is different.
            * 3. not playing or yes playing.
            * 4.
            * */
            case SYNC_PARTY:
                JSONArray users = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.USERS);
                // no party image at the moment.
                //JSONArray image = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.IMAGE);
                JSONArray requests = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.REQUESTS);
                JSONArray songs = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.SONGS);
                JSONArray name = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.NAME);
                JSONArray is_private = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.IS_PRIVATE);
                JSONArray cur_track = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.CURRENT_TRACK_ID);
                boolean move = false;
                if (app.client_manager.party == null) {
                    app.client_manager.party = new Party();
                    move = true;
                }
                app.client_manager.party.is_private = is_private.getBoolean(0);
                app.client_manager.party.party_name = name.getString(0);
                updateUserList(getUserListFromJSON(users),app.client_manager.party);
                app.client_manager.party.request = getUserListFromJSON(requests);

                if(app.client_manager.party.playlist != null) {
                    int prev_size = app.client_manager.party.playlist.tracks.size();
                    Log.d("MediaManager", "prev playlist size = "+prev_size);
                    app.client_manager.party.playlist.tracks = getTrackListFromJSON(songs);
                    if(prev_size == 1 && app.client_manager.party.playlist.tracks.size() > 1){
                        Log.d("MediaManager", "prepare 2nd song");
                        app.media_manager.prepare2nd(app.client_manager.party.playlist.tracks.get(
                                (app.client_manager.party.playlist.cur_track + 1)%
                                app.client_manager.party.playlist.tracks.size()).track_id);
                    }

                }
                else
                    app.client_manager.party.playlist = new Playlist(getTrackListFromJSON(songs), false, 0);
                app.client_manager.party.playlist.cur_track = posFromTrackId(cur_track);

                if(move)
                    app.gui_manager.completeJoin();
                else
                    app.gui_manager.syncParty();

                break;

            case SEARCH_RESULT:
                JSONArray parties = CommandClientAux.getPartyArray(cmd);
                List<partyInfo> party_list = getPartyListFromJSON(parties);
                app.gui_manager.putPartyResults(party_list);
                break;

            case GET_READY:
                int prep_track_id = cmd.getIntAttribute(jsonKey.TRACK_ID);
                int prep_offset = cmd.getIntAttribute(jsonKey.OFFSET);
                app.media_manager.getReady(prep_track_id, prep_offset);
                break;

            case PLAY_SONG:
                int play_track_id = cmd.getIntAttribute(jsonKey.TRACK_ID);
                int play_offset = cmd.getIntAttribute(jsonKey.OFFSET);
                app.gui_manager.play(play_track_id);
                app.media_manager.play(play_track_id,play_offset);
                break;

            case PAUSE:
                app.gui_manager.pause();
                app.media_manager.pause();
                break;

            case REJECTED:
                app.gui_manager.rejected();
                break;
            /*case CLOSE_PARTY:
                if(openparty) {
                    app.gui_manager.closeParty();
                    openparty = false;
                }
                break;*/

            case DISCONNECTED:
                if (!disconnected ) {
                    app.gui_manager.disconnected();
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
