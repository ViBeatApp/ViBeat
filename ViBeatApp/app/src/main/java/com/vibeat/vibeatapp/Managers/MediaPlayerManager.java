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
    }

    public boolean getReadyOneSong(int new_active_mp, int track_id, int offset, boolean joiningPlayingParty) throws IOException{
        if (players[new_active_mp].track_id == track_id) {
            players[new_active_mp].getReady(track_id, offset, joiningPlayingParty);
            players[1 - new_active_mp].reset();
            active_mp = new_active_mp;
            return true;
        }
        return false;
    }

    public void getReady(int track_id, int offset,  boolean joiningPlayingParty) {
        if (active_mp == -1) { // first time of get-ready -> init active_mp
            active_mp = 0;
        }
        try {
            int num_tracks = app.client_manager.party.playlist.tracks.size();
            int next_track = app.client_manager.party.playlist.tracks.get((
                    app.client_manager.getTrackPosFromId(track_id) + 1) %
                    app.client_manager.party.playlist.tracks.size()).track_id;

            if (num_tracks == 0) {
                return; //ERROR
            } else if (num_tracks == 1) {
                if ((players[0].track_id != track_id) && (players[1].track_id != track_id)) {
                    players[0].getReady(track_id, offset, joiningPlayingParty);
                    players[1].reset();
                    active_mp = 0;
                } else { // one of the mp is set to the right song-id
                    if (!getReadyOneSong(0, track_id, offset, joiningPlayingParty)) {
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
                    if (players[active_mp].track_id != track_id) {
                        players[active_mp].pause();
                        active_mp = 1 - active_mp; // changing the active_mp: 1 -> 0, 0 -> 1
                    }
                    players[active_mp].getReady(track_id, offset, joiningPlayingParty);
                    players[1 - active_mp].getReady(next_track, 0, false);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void prepare2nd(int next_track) {
        if(active_mp == -1) return;
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
        if((active_mp != -1) && (getPlayingTrackId() == track_id)) {
            return players[active_mp].getCurrentPosition();
        }
        return 0;
    }

    public void pause() {
        if (active_mp != -1) {
            if(players[active_mp].isPlaying())
                players[active_mp].pause();
        }
    }

    public boolean isMute() {
        if (active_mp != -1)
            return  players[active_mp].is_mute;
        return false;
    }

    public void stop() {
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
}


