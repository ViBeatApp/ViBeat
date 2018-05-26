package com.vibeat.vibeatapp.HelperClasses;

import com.vibeat.vibeatapp.Command;
import com.vibeat.vibeatapp.CommandClientAux;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.jsonKey;
import com.vibeat.vibeatapp.partyInfo;
import com.vibeat.vibeatapp.readWriteAux;
import com.vibeat.vibeatapp.trackInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ListenerThread extends Thread {

    public readWriteAux readWriteAux;
    public  MyApplication app;

    public ListenerThread(MyApplication app, readWriteAux readWriteAux) {
        this.readWriteAux = readWriteAux;
        this.app = app;
    }


    @Override
    public void run() {

        try{ /*connection test*/ } catch(Exception e){}

        while (!this.isInterrupted()) {
            Command cmd = null;
            try {
                cmd = getServerCommand();
                handlerCommand(cmd);
            }
            catch (InterruptedException e){
                break;
            } catch (JSONException e){
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

        switch (cmd.cmd_type){

            case SYNC_PARTY:
                JSONArray users = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.USERS);
                // no party image at the moment.
                //JSONArray image = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.IMAGE);
                JSONArray requests = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.REQUESTS);
                JSONArray songs = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.SONGS);
                JSONArray name = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.NAME);
                JSONArray is_private = CommandClientAux.getSyncPartyAttribute(cmd , jsonKey.IS_PRIVATE);

                boolean move = false;
                if (app.client_manager.party == null) {
                    app.client_manager.party = new Party();
                    move = true;
                }

                app.client_manager.party.is_private = is_private.getBoolean(0);
                app.client_manager.party.party_name = name.getString(0);

                updateUserList(getUserListFromJSON(users),app.client_manager.party);
                app.client_manager.party.request = getUserListFromJSON(requests);

                if(app.client_manager.party.playlist != null)
                    app.client_manager.party.playlist.tracks = getTrackListFromJSON(songs);
                else
                    app.client_manager.party.playlist = new Playlist(getTrackListFromJSON(songs), false, 0);

                if(move)
                    app.gui_manager.completeJoin();
                else
                    app.gui_manager.syncParty();
                break;

            case SEARCH_RESULT:
                JSONArray parties = CommandClientAux.getPartyArray(cmd);
                List<Party> party_list = null;
                app.gui_manager.putPartyResults(party_list);

            case GET_READY:
                int prep_track_id = cmd.getIntAttribute(jsonKey.TRACK_ID);
                int prep_offset = cmd.getIntAttribute(jsonKey.OFFSET);
                app.media_manager.prepare(prep_offset , app.client_manager.party.playlist.searchTrack(prep_track_id));
                break;
            case PLAY_SONG:
                int play_track_id = cmd.getIntAttribute(jsonKey.TRACK_ID);
                int play_offset = cmd.getIntAttribute(jsonKey.OFFSET);
                app.gui_manager.play(play_track_id);
                app.media_manager.play(play_offset , app.client_manager.party.playlist.searchTrack(play_track_id));
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
