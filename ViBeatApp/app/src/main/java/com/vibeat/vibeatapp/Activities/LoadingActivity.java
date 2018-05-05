package com.vibeat.vibeatapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {

    public MyApplication app;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        app = (MyApplication) this.getApplication();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent;
                boolean answer = app.client_manager.connectParty();
                if (answer)
                     intent = new Intent(LoadingActivity.this, PlaylistActivity.class);
                else
                    intent = new Intent(LoadingActivity.this, EnterPartyActivity.class);
                startActivity(intent);
            }
        }, 4*1000);

    }
}
