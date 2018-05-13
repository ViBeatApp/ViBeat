package com.vibeat.vibeatapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        app.listener_thread.current_activity = LoadingActivity.this;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent;
                app.client_manager.connectParty();
            }
        }, 4*1000);

        Button nevermind = (Button) findViewById(R.id.nevermind);
        nevermind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadingActivity.this, EnterPartyActivity.class);
                startActivity(intent);
            }
        });
    }
}
