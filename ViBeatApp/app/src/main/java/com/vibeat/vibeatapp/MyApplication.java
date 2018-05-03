package com.vibeat.vibeatapp;

import android.app.Application;

import com.vibeat.vibeatapp.HelperClasses.ClientManager;
import com.vibeat.vibeatapp.HelperClasses.MediaPlayerManager;

public class MyApplication extends Application {
    public ClientManager client_manager = null;
    public MediaPlayerManager media_manager = null;
}
