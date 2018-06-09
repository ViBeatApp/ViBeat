package com.vibeat.vibeatapp.Managers;

import android.util.Log;

import com.vibeat.vibeatapp.HelperClasses.MyMediaPlayer;
import com.vibeat.vibeatapp.MyApplication;

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

    public void getReady(int track_id, int offset){
        try {
            int num_tracks = app.client_manager.party.playlist.tracks.size();
            int next_track = app.client_manager.party.playlist.tracks.get((
                    app.client_manager.getTrackPosFromId(track_id) + 1) %
                    app.client_manager.party.playlist.tracks.size()).track_id;
            Log.d("GET_READY","#tracks = "+num_tracks+" , next_track_id = "+next_track);

            if (num_tracks == 0) {
                return; //ERROR
            } else if (num_tracks == 1) {
                if (track_id == m1.track_id) {
                    m1.getReady(track_id, offset);
                    active_mp = 1;
                }
                else if (track_id == m2.track_id) {
                    m2.getReady(track_id, offset);
                    active_mp = 2;
                }
                else{
                    m1.getReady(track_id, offset);
                    active_mp = 1;
                }
            } else {
                // if there was a pause command, m1.id == track_id or m2.id == track_id because we already played it.
                Log.d("GET_READY","#tracks = "+num_tracks+" , next_track_id = "+next_track);
                if (track_id == m1.track_id) {
                    Log.d("GET_READY","1");
                    m1.getReady(track_id, offset);
                    m2.getReady(next_track, 0);
                } else if (track_id == m2.track_id) {
                    Log.d("GET_READY","2");
                    m2.getReady(track_id, offset);
                    m1.getReady(next_track, 0);
                }
                // new songs to prepare on.
                else {
                    Log.d("GET_READY","else");
                    m1.getReady(track_id, offset);
                    m2.getReady(next_track, 0);
                }
            }
        }
        catch (IOException e){
            Log.e("MediaManager","getReady error");
            e.printStackTrace();
        }
    }

    public void prepare2nd(int next_track) {
        try {
            if(active_mp == 1)
                m2.getReady(next_track, 0);
            else
                m1.getReady(next_track, 0);
        }
        catch (IOException e) {
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

    public int getOffset(int track_id) {
        if(getPlayingTrackId()==track_id) {
            if (active_mp == 2)
                return m2.getCurrentPosition();
            else
                return m1.getCurrentPosition();
        }
        else
            return 0;
    }

    public void pause() {
        Log.d("pause", "before pause");
        if(m1.isPlaying())
            m1.pause();
        if(m2.isPlaying())
            m2.pause();
        //m1.pause();
        //m2.pause();
        Log.d("pause", "after pause");
        /*if(active_mp == 2)
            m2.pause();
        else
            m1.pause();*/
    }

    public boolean isMute() {
        return m1.is_mute;
    }

    public void stop() {
        Log.d("GET_READY","stop");
        if(active_mp == 2)
            m2.reset();
        else
            m1.reset();
    }

    //optimizations in the future
    /*public void preparedNextSong() {
        Timer timer = new Timer();
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {

            }
        };

        timer.schedule(myTask, 1000, 1000);
    }*/

    public int getPlayingTrackId(){
        if(active_mp == 2)
            return m2.track_id;
        else if (active_mp == 1)
            return m1.track_id;
        return -1;
    }
}
