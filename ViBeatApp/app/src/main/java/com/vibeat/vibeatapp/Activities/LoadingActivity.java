package com.vibeat.vibeatapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vibeat.vibeatapp.HelperClasses.passingInfo;
import com.vibeat.vibeatapp.R;

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

        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra("info",info);
        startActivity(intent);
    }


}
