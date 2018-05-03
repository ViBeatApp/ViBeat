package com.vibeat.vibeatapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.vibeat.vibeatapp.HelperClasses.AuthenticationManager;
import com.vibeat.vibeatapp.HelperClasses.ClientManager;
import com.vibeat.vibeatapp.HelperClasses.passingInfo;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;

public class MainActivity extends AppCompatActivity {

    public MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (MyApplication) this.getApplication();

        ImageButton facebook = (ImageButton) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager = new ClientManager(AuthenticationManager.getFacebookUser());
                login();
            }
        });

        ImageButton google = (ImageButton) findViewById(R.id.google);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager = new ClientManager(AuthenticationManager.getGoogleUser());
                login();
            }
        });
    }

    public void login() {
        Intent intent = new Intent(this, EnterPartyActivity.class);
        startActivity(intent);
    }
}