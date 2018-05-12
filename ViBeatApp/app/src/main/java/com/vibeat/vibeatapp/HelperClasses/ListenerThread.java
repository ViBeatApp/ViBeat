package com.vibeat.vibeatapp.HelperClasses;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.vibeat.vibeatapp.Activities.EnterPartyActivity;
import com.vibeat.vibeatapp.Activities.LoadingActivity;
import com.vibeat.vibeatapp.Activities.PlaylistActivity;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.ServerMsg;

import static com.vibeat.vibeatapp.Objects.MSGType.*;


public class ListenerThread extends Thread {

    public Activity current_activity;
    public Intent intent;

    public ListenerThread(Activity current_activity) {
        this.current_activity = current_activity;
    }

    @Override
    public void run() {
        while (true) {
            ServerMsg m = getServerMSG();
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

    public ServerMsg getServerMSG() {
        try {
            this.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ServerMsg(AddAdmin, true);
    }
}
