package com.vibeat.vibeatapp.Managers;

import android.util.Log;

import com.vibeat.vibeatapp.HelperClasses.MyMediaPlayer;
import com.vibeat.vibeatapp.MyApplication;

import java.io.IOException;

public class MediaPlayerManager {

    public MyApplication app;

    public MyMediaPlayer m[];
    public MyMediaPlayer m1;
    public MyMediaPlayer m2;
    public int active_mp = 0;

    public MediaPlayerManager(MyApplication app) {
        this.app = app;
        m = new MyMediaPlayer[2];
        m1 = new MyMediaPlayer(app, 1);
        m2 = new MyMediaPlayer(app, 2);
    }

    public void getReady(int track_id, int offset,boolean joiningPlayingParty){
        Log.d("Test1","inside get ready of Media Manager");
        try {
            int num_tracks = app.client_manager.party.playlist.tracks.size();
            int next_track = app.client_manager.party.playlist.tracks.get((
                    app.client_manager.getTrackPosFromId(track_id) + 1) %
                    app.client_manager.party.playlist.tracks.size()).track_id;
            Log.d("Test1","#tracks = "+num_tracks+" , next_track_id = "+next_track);

            if (num_tracks == 0) {
                Log.d("Test1","Error - 0 songs in playlist");
                return; //ERROR
            } else if (num_tracks == 1) {
                Log.d("DebugMediaPlayer", "num of tracks == 1");
                if (track_id == m1.track_id) {
                    Log.d("DebugMediaPlayer", "getReady: track_id == m1.track_id");
                    m1.getReady(track_id, offset,joiningPlayingParty);
                    m2.reset();
                    active_mp = 1;
                }
                else if (track_id == m2.track_id) {
                    Log.d("DebugMediaPlayer", "getReady: track_id == m2.track_id");
                    m2.getReady(track_id, offset,joiningPlayingParty);
                    m1.reset();
                    active_mp = 2;
                }
                else{
                    Log.d("DebugMediaPlayer","getReady() - first get-ready");
                    m1.getReady(track_id, offset,joiningPlayingParty);
                    m2.reset();
                    active_mp = 1;
                }
            } else {
                // if there was a pause command, m1.id == track_id or m2.id == track_id because we already played it.
                if (track_id == m1.track_id) {
                    m1.getReady(track_id, offset,joiningPlayingParty);
                    if(active_mp == 2)
                        m2.pause();
                    m2.getReady(next_track, 0,false);
                } else if (track_id == m2.track_id) {
                    m2.getReady(track_id, offset,joiningPlayingParty);
                    if(active_mp == 1)
                        m1.pause();
                    m1.getReady(next_track, 0,false);
                }
                // new songs to prepare on.
                else {
                    m1.pause();
                    m2.pause();
                    m1.getReady(track_id, offset,joiningPlayingParty);
                    m2.getReady(next_track, 0,false);
                }
            }
        }
        catch (IOException e){
            Log.e("Test1","getReady error");
            e.printStackTrace();
        }
    }

    public void prepare2nd(int next_track) {
        try {
            if(active_mp == 1)
                m2.getReady(next_track, 0,false);
            else
                m1.getReady(next_track, 0,false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(int track_id, int offset){
        try {
            if (active_mp == 1) {
                if (m1.track_id == track_id) {
                    Log.e("Test1", "inside if1 in play");
                    m1.play(track_id, offset);
                }
                else {
                    Log.e("Test1","inside else1 in play");
                    m2.play(track_id, offset);
                    active_mp = 2;
                }
            } else if (active_mp == 2) {
                Log.e("Test1","inside if2 in play");
                if (m2.track_id == track_id)
                    m2.play(track_id, offset);
                else {
                    Log.e("Test1","inside else2 in play");
                    m1.play(track_id, offset);
                    active_mp = 1;
                }
            } else {
                Log.e("Test1","inside else3 in play");
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
            else if(active_mp == 1)
                return m1.getCurrentPosition();
            else
                return 0;
        }
        else
            return 0;
    }

    public void pause() {
        Log.d("Test1", "before pause");
        if(m1.isPlaying())
            m1.pause();
        if(m2.isPlaying())
            m2.pause();
        Log.d("Test1", "after pause");
    }

    public boolean isMute() {
        return m1.is_mute;
    }

    public void stop() {
        Log.d("Test1","stop");
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
