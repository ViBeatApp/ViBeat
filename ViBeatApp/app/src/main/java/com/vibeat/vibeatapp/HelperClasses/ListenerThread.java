package com.vibeat.vibeatapp.HelperClasses;

import android.util.Log;

import com.vibeat.vibeatapp.ServerSide.Command;
import com.vibeat.vibeatapp.ServerSide.CommandClientAux;
import com.vibeat.vibeatapp.Managers.DBManager;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.ServerSide.jsonKey;
import com.vibeat.vibeatapp.ServerSide.partyInfo;
import com.vibeat.vibeatapp.ServerSide.ReadWriteAux;
import com.vibeat.vibeatapp.ServerSide.trackInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ListenerThread extends Thread {

    public ReadWriteAux readWriteAux;
    public  MyApplication app;

    public ListenerThread(MyApplication app, ReadWriteAux readWriteAux) {
        this.readWriteAux = readWriteAux;
        this.app = app;
    }


    @Override
    public void run() {

        try{ /*connection test*/ } catch(Exception e){}

        while (true) {
            Command cmd = null;
            try {
                Log.e("Listener","before listen");
                cmd = getServerCommand();
                Log.e("Listener","atfer listen");
                handlerCommand(cmd);
            }
            catch (InterruptedException e){
                break;
            } catch (JSONException e){
                break;
            }
            try{Thread.sleep(500);}catch (Exception e){}
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

            case SYNC_PARTY:
                Log.e("Listener","0");
                JSONArray users = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.USERS);
                Log.e("Listener","0.1");
                // no party image at the moment.
                //JSONArray image = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.IMAGE);
                JSONArray requests = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.REQUESTS);
                Log.e("Listener","0.2");
                JSONArray songs = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.SONGS);
                Log.e("Listener","0.3");
                JSONArray name = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.NAME);
                Log.e("Listener","0.4");
                JSONArray is_private = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.IS_PRIVATE);
                Log.e("Listener","1");
                boolean move = false;
                if (app.client_manager.party == null) {
                    app.client_manager.party = new Party();
                    move = true;
                }
                Log.e("Listener","2");
                app.client_manager.party.is_private = is_private.getBoolean(0);
                app.client_manager.party.party_name = name.getString(0);
                Log.e("Listener","3");
                updateUserList(getUserListFromJSON(users),app.client_manager.party);
                app.client_manager.party.request = getUserListFromJSON(requests);
                Log.e("Listener","4");
                if(app.client_manager.party.playlist != null)
                    app.client_manager.party.playlist.tracks = getTrackListFromJSON(songs);
                else
                    app.client_manager.party.playlist = new Playlist(getTrackListFromJSON(songs), false, 0);
                Log.e("Listener","5");
                if(move)
                    app.gui_manager.completeJoin();
                else
                    app.gui_manager.syncParty();
                Log.e("Listener","6");

                break;

            case SEARCH_RESULT:
                JSONArray parties = CommandClientAux.getPartyArray(cmd);
                List<Party> party_list = getPartyListFromJSON(parties);
                app.gui_manager.putPartyResults(party_list);

            case GET_READY:
                Log.e("Listener","getReady");
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
            case CLOSE_PARTY:
                break;
            case DISCONNECTED:
                break;

        }
    }


    private List<Track> getTrackListFromJSON(JSONArray arr) throws JSONException{
        List<Track> tracks = new ArrayList<Track>();
        for (int i = 0; i < arr.length(); i++ ){
            trackInfo s = (trackInfo)arr.get(i);
            Track track = DBManager.getTrackByURL(s.track_path, s.track_id);
            tracks.add(track);
        }
        return tracks;
    }

    private List<User> getUserListFromJSON(JSONArray arr) throws JSONException{
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < arr.length(); i++ ){
            User u = (User)arr.get(i);
            users.add(u);
        }
        return users;
    }

    private List<Party> getPartyListFromJSON(JSONArray arr) throws JSONException{
        List<Party> parties = new ArrayList<Party>();
        for (int i = 0; i < arr.length(); i++ ){
            partyInfo p = (partyInfo)arr.get(i);
            Party party = new Party();
            party.party_name = p.party_name;
            party.id = p.id;
            parties.add(party);
            parties.add(party);
        }
        return parties;
    }

    private void updateUserList(List<User> users, Party party){
        for (User user : users){
            if (user.is_admin)
                party.admin.add(user);
            else
                party.connected.add(user);
        }
    }
}
