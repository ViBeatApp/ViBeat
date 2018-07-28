package com.vibeat.vibeatapp;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.vibeat.vibeatapp.Managers.ClientManager;
import com.vibeat.vibeatapp.Managers.FBManager;
import com.vibeat.vibeatapp.ServerConnection.ListenerThread;
import com.vibeat.vibeatapp.Managers.MediaPlayerManager;
import com.vibeat.vibeatapp.ServerConnection.SenderThread;
import com.vibeat.vibeatapp.Managers.GUIManager;

import java.util.concurrent.Semaphore;

public class MyApplication extends Application {
    public ClientManager client_manager = null;
    public MediaPlayerManager media_manager = null;
    public ListenerThread listener_thread = null;
    public SenderThread sender_thread = null;
    public GUIManager gui_manager = null;
    public FBManager fb_manager = null;
    public Semaphore semaphore = null;
    public Semaphore semaphoreSender = null;
    public Semaphore semaphoreDisconnected = null;
    public boolean disconnected = true;
    public GoogleSignInClient mGoogleSignInClient = null;
    public boolean sync_music = false;
}
