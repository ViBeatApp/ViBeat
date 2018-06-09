package com.vibeat.vibeatapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Adapter;

import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;

import java.util.List;
import java.util.Timer;

public class LoadingActivity extends AppCompatActivity {

    public MyApplication app;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        app = (MyApplication) this.getApplication();
        app.gui_manager.changeActivity(LoadingActivity.this,(List<Adapter>) null);
        app.gui_manager.initLoadingActivity();
    }

    @Override
    public void onBackPressed(){
        app.client_manager.leaveParty();
        Intent intent = new Intent(this, EnterPartyActivity.class);
        startActivity(intent);
    }
}
