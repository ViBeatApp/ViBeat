package com.vibeat.vibeatapp.HelperClasses;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.vibeat.vibeatapp.Activities.EnterPartyActivity;
import com.vibeat.vibeatapp.Activities.PlaylistActivity;
import com.vibeat.vibeatapp.Command;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.ServerMsg;
import com.vibeat.vibeatapp.jsonKey;
import com.vibeat.vibeatapp.test;
import com.vibeat.vibeatapp.readWriteAux;

import org.json.JSONException;

import java.io.IOException;

import static com.vibeat.vibeatapp.CommandType.*;


public class ListenerThread extends Thread {

    public Activity current_activity;
    public  MyApplication app;
    public Intent intent;

    public MediaPlayerManager media_manager;
    public readWriteAux readWriteAux;

    public ListenerThread(Activity current_activity, readWriteAux readWriteAux) {

        this.current_activity = current_activity;
        app = (MyApplication) current_activity.getApplication();
        media_manager = ((MyApplication) current_activity.getApplication()).media_manager;
        this.readWriteAux = readWriteAux;
    }

    @Override
    public void run() {

        try{
            test.test();}catch(Exception e){}

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
            if (m.msg_type == RequestAnswer) {
                if (m.bool_info) {

                    intent = new Intent(current_activity, PlaylistActivity.class);
                    current_activity.startActivity(intent);
                }
                else {
                    Toast.makeText(current_activity,
                            "Sorry, your request was not accepted...",
                            Toast.LENGTH_LONG).show();
                    intent = new Intent(current_activity, EnterPartyActivity.class);
                    current_activity.startActivity(intent);
                }
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
            case AUTHENTICATION:
                break;
            case NEARBY_PARTIES:
                cmd.getSyncPartyAttribute(jsonKey.PARTY_INFO.name());
            case DISCONNECTED:
                break;

            case ADD_SONG:
                int add_id = cmd.getIntAttribute(jsonKey.TRACK_ID.name());
                app.client_manager.party.playlist.addTrack(DBManager.getTrack(add_id));
                break;
            case DELETE_SONG:
                int remove_id = cmd.getIntAttribute(jsonKey.TRACK_ID.name());
                app.client_manager.party.playlist.tracks.remove(remove_id);
                break;
            case SWAP_SONGS:
                    break;

            case GET_READY:
                break;
            case PLAY_SONG:
                    break;
            case PAUSE:
                    break;

            case RENAME_PARTY:
                String party_name = cmd.getStringAttribute(jsonKey.PARTY_RENAME.name());
                app.client_manager.party.party_name = party_name;
                break;
            case MAKE_PRIVATE:
                boolean is_private = cmd.getBoolAttribute(jsonKey.IS_PRIVATE.name());
                if(!is_private) {
                    app.client_manager.party.connected.addAll(app.client_manager.party.request);
                    app.client_manager.party.request.clear();
                }
                app.client_manager.party.is_private = is_private;
                break;
            case CONFIRM_REQUEST:
                    break;
            case CLOSE_PARTY:
                    break;


            case REJECTED:
                    break;
            case UPDATE_PARTY:
                    break;
            case SYNC_PARTY:
                    break;
            case SEARCH_RESULT:
                    break;
        }
    }
}
