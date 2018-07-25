package com.vibeat.vibeatapp.Managers;

import android.util.Log;

import com.vibeat.vibeatapp.HelperClasses.MyMediaPlayer;
import com.vibeat.vibeatapp.MyApplication;

import java.io.IOException;

public class MediaPlayerManager {

    public MyApplication app;

    public MyMediaPlayer players[];
    public MyMediaPlayer m1;
    public MyMediaPlayer m2;
    public int active_mp = -1;

    public MediaPlayerManager(MyApplication app) {
        this.app = app;
        players = new MyMediaPlayer[2];
        players[0] = new MyMediaPlayer(app, 0);
        players[1] = new MyMediaPlayer(app, 1);
        //m1 = new MyMediaPlayer(app, 1);
        //m2 = new MyMediaPlayer(app, 2);
    }

    public boolean getReadyOneSong(int new_active_mp, int track_id, int offset, boolean joiningPlayingParty) throws IOException{
        if (players[new_active_mp].track_id == track_id) {
            Log.d("DebugMediaPlayer", "getReady: track_id == " + new_active_mp + " current active_mp = " + active_mp);
            players[new_active_mp].getReady(track_id, offset, joiningPlayingParty);
            players[1 - new_active_mp].reset();
            active_mp = new_active_mp;
            return true;
        }
        return false;
    }

    public void getReady(int track_id, int offset,  boolean joiningPlayingParty) {
        Log.d("active_mp","inside get ready of Media Manager, active-mp: " + active_mp);
        if (active_mp == -1) { // first time of get-ready -> init active_mp
            active_mp = 0;
        }
        try {
            int num_tracks = app.client_manager.party.playlist.tracks.size();
            int next_track = app.client_manager.party.playlist.tracks.get((
                    app.client_manager.getTrackPosFromId(track_id) + 1) %
                    app.client_manager.party.playlist.tracks.size()).track_id;
            Log.d("Test1","#tracks = " + num_tracks + " , next_track_id = " + next_track);

            if (num_tracks == 0) {
                Log.d("Test1","Error - 0 songs in playlist");
                return; //ERROR
            } else if (num_tracks == 1) {
                if ((players[0].track_id != track_id) && (players[1].track_id != track_id)) {
                    players[0].getReady(track_id, offset, joiningPlayingParty);
                    players[1].reset();
                    active_mp = 0;
                } else { // one of the mp is set to the right song-id
                    Log.d("active_mp","one player is prepared ");
                    if (!getReadyOneSong(0, track_id, offset, joiningPlayingParty)) {
                        Log.d("active_mp","changed active-mp: " + active_mp);
                        getReadyOneSong(1, track_id, offset, joiningPlayingParty);
                    }
                }
            } else { // if there was a pause command, m1.id == track_id or m2.id == track_id because we already played it.
                if ((players[0].track_id != track_id) && (players[1].track_id != track_id)) { // new songs to prepare on
                    players[0].pause();
                    players[1].pause();
                    players[0].getReady(track_id, offset, joiningPlayingParty);
                    players[1].getReady(next_track, 0,false);
                    active_mp = 0;
                } else {
                    Log.d("active_mp"," > 1 songs");
                    if (players[active_mp].track_id != track_id) {
                        Log.d("active_mp"," changing active-mp");
                        players[active_mp].pause();
                        active_mp = 1 - active_mp; // changing the active_mp: 1 -> 0, 0 -> 1
                    }
                    players[active_mp].getReady(track_id, offset, joiningPlayingParty);
                    players[1 - active_mp].getReady(next_track, 0, false);
                }
            }
        } catch (IOException e){
            Log.e("Test1","getReady error");
            e.printStackTrace();
        }
    }

    public void prepare2nd(int next_track) {
        try {
            players[1 - active_mp].getReady(next_track, 0, false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mute() {
        players[0].mute();
        players[1].mute();
    }

    public void unmute() {
        players[0].unmute();
        players[1].unmute();
    }

    public int getOffset(int track_id) {
        Log.d("getOffset", "before pause");
        if((active_mp != -1) && (getPlayingTrackId() == track_id)) {
            return players[active_mp].getCurrentPosition();
        }
        return 0;
    }

    public void pause() {
        Log.d("Test1", "before pause");
        if (active_mp != -1) {
            if(players[active_mp].isPlaying())
                players[active_mp].pause();
        }
        Log.d("Test1", "after pause");
    }

    public boolean isMute() {
        if (active_mp != -1)
            return  players[active_mp].is_mute;
        return false;
    }

    public void stop() {
        //Log.d("Test1","stop");
        if (active_mp != -1) {
            players[active_mp].reset();
        }
    }

    public void play(int track_id, int offset){
        try {
            if (active_mp != -1) {
                if (players[active_mp].track_id != track_id) {
                    active_mp = 1 - active_mp;
                }
                players[active_mp].play(track_id, offset);
            } else {
                Log.d("Play", "----------- error: trying to play without media player -------------");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public int getPlayingTrackId(){
        if (active_mp != -1) {
            return players[active_mp].track_id;
        }
        return -1;
    }

    public void release_all() {
        players[0].release();
        players[1].release();

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

    public void play_old(int track_id, int offset){
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

    public void getReady_old(int track_id, int offset,boolean joiningPlayingParty){
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
                    m1.getReady(track_id, offset, joiningPlayingParty);
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
}


