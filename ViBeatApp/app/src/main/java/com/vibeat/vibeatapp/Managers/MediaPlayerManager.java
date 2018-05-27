package com.vibeat.vibeatapp.Managers;

import android.app.Application;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.vibeat.vibeatapp.HelperClasses.MyMediaPlayer;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Playlist;

import java.io.IOException;

public class MediaPlayerManager {

    public MyApplication app;

    public MyMediaPlayer m1;
    public MyMediaPlayer m2;
    int active_mp = 0;

    public MediaPlayerManager(MyApplication app) {
        this.app = app;
        m1 = new MyMediaPlayer(app);
        m2 = new MyMediaPlayer(app);
    }

    public void getReady(int track_id, int offset) {
        try {
            Log.e("MediaManager","before m.getReady");
            if (active_mp == 1)
                m2.getReady(track_id, offset);
            else
                m1.getReady(track_id, offset);
        }
        catch (IOException e){
            Log.e("MediaManager","getReady error");
            e.printStackTrace();
        }
    }

    public void play(int track_id, int offset){
        Log.e("MediaManager","playing");
        try {
            if (active_mp == 1) {
                if (m1.track_id == track_id)
                    m1.play(track_id, offset);
                else {
                    m1.reset();
                    m2.play(track_id, offset);
                    active_mp = 2;
                }
            } else if (active_mp == 2) {
                if (m2.track_id == track_id)
                    m2.play(track_id, offset);
                else {
                    m2.reset();
                    m1.play(track_id, offset);
                    active_mp = 1;
                }
            } else {
                m1.play(track_id, offset);
                active_mp = 1;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void mute() {
        m1.mute();
        m2.mute();
    }

    public void unmute() {
        m1.unmute();
        m2.unmute();
    }

    public int getOffset() {
        if(active_mp == 2)
            return m2.getCurrentPosition();
        else
            return m1.getCurrentPosition();
    }

    public void pause() {
        if(active_mp == 2)
            m2.pause();
        else
            m1.pause();
    }

    public boolean isMute() {
        return m1.is_mute;
    }
}
