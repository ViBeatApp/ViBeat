package com.vibeat.vibeatapp.HelperClasses;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.vibeat.vibeatapp.MyApplication;

import java.io.IOException;

public class MyMediaPlayer extends MediaPlayer {

    MyApplication app;
    public int track_id = -1;
    public boolean is_mute = false;
    public boolean preparing = false;
    public boolean is_prepared = false;
    public int offset = 0;

    public MyMediaPlayer(final MyApplication app){
        super();
        this.app = app;

        this.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                whenPrepared();
            }
        });

        this.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(isCurTrack())
                    app.client_manager.nextSong();
            }
        });

        this.unmute();
    }

    private synchronized void whenPrepared() {
        is_prepared = true;
        preparing = false;
        this.seekTo(this.offset);
        Log.e("MediaManager", "after prepare");
        if(isCurTrack())
            app.client_manager.sendReady(track_id);
    }

    public synchronized void setCurrentTrack(int track_id)throws IOException {

        this.track_id = track_id;
        this.reset();
        this.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.setDataSource(app.client_manager.getURLByTrackId(track_id));

    }

    public synchronized void getReady(int track_id, int offset)throws IOException {
        this.offset = offset;
        Log.e("MediaManager", "before prepare async");
        if (is_prepared && this.track_id == track_id)
            app.client_manager.sendReady(track_id);
        else if (!preparing || this.track_id != track_id) {
            Log.e("MediaManager", "inside prepare async");
            setCurrentTrack(track_id);
            preparing = true;
            is_prepared = false;
            this.prepareAsync();
        }
        Log.e("MediaManager", "after prepare async");

    }

    public synchronized void play(int track_id, int offset) throws IOException {
        if(this.offset != offset) {
            this.offset = offset;
            this.seekTo(this.offset);
        }
        if (this.track_id != track_id || !is_prepared) {
            setCurrentTrack(track_id);
            this.prepare();
            this.is_prepared = true;
        }
        this.start();

    }

    public void mute(){
        this.setVolume(0, 0 );
        is_mute = true;
    }

    public void unmute(){
        this.setVolume(1, 1 );
        is_mute = false;
    }

    public boolean isCurTrack(){
        return app.client_manager.getTrackPosFromId(this.track_id) == app.client_manager.party.playlist.cur_track;
    }

}
