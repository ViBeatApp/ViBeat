package com.vibeat.vibeatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {

    passingInfo info;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent i = getIntent();
        info = (passingInfo)i.getSerializableExtra("info");


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                load();
            }
        }, 4*1000);

    }

    public void load() {

        Intent intent = new Intent(this, playlistActivity.class);
        intent.putExtra("info",info);
        startActivity(intent);
    }


}
