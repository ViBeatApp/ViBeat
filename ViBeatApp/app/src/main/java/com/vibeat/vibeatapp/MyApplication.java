package com.vibeat.vibeatapp;

import android.app.Application;

import com.vibeat.vibeatapp.HelperClasses.ClientManager;
import com.vibeat.vibeatapp.HelperClasses.ListenerThread;
import com.vibeat.vibeatapp.HelperClasses.MediaPlayerManager;
import com.vibeat.vibeatapp.HelperClasses.SenderThread;
import com.vibeat.vibeatapp.ListClasses.GUIManager;

public class MyApplication extends Application {
    public ClientManager client_manager = null;
    public MediaPlayerManager media_manager = null;
    public ListenerThread listener_thread = null;
    public SenderThread sender_thread = null;
    public GUIManager gui_manager = null;
}
