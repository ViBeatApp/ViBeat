package com.vibeat.vibeatapp.HelperClasses;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.vibeat.vibeatapp.Activities.EnterPartyActivity;
import com.vibeat.vibeatapp.Activities.LoadingActivity;
import com.vibeat.vibeatapp.Activities.PlaylistActivity;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.ServerMsg;

import static com.vibeat.vibeatapp.Objects.MSGType.*;


public class ListenerThread extends Thread {

    public Activity current_activity;
    public MediaPlayerManager media_manager;
    public Intent intent;

    public ListenerThread(Activity current_activity) {
        this.current_activity = current_activity;
        media_manager = ((MyApplication) current_activity.getApplication()).media_manager;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            ServerMsg m = null;
            try {
                m = getServerMSG();
            }
            catch (InterruptedException e){
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

    public ServerMsg getServerMSG() throws InterruptedException{

        this.sleep(2000);
        return new ServerMsg(AddAdmin, true);
    }
}
