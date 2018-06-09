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
                Log.e("MediaManager", "on prepared");
                whenPrepared();
            }
        });

        this.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("GET_READY", "on complete "+app.client_manager.party.playlist.tracks.get(
                        app.client_manager.party.playlist.cur_track).track_id);
                if(isCurTrack())
                    app.client_manager.nextSong();
            }
        });

        /*this.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("GET_READY", "on error "+app.client_manager.party.playlist.tracks.get(
                        app.client_manager.party.playlist.cur_track).track_id);
                return true;
            }
        });*/

        this.unmute();
    }

    private synchronized void whenPrepared() {
        Log.e("MediaManager", "inside when prepared");
        if(app.client_manager.party != null) {
            Log.e("MediaManager", "inside when prepared if statement");
            is_prepared = true;
            preparing = false;
            this.seekTo(this.offset);
            Log.e("MediaManager", "after prepare");
            if (isCurTrack())
                app.client_manager.sendReady(track_id);
        }
    }

    public synchronized void setCurrentTrack(int track_id)throws IOException {
        Log.d("GET_READY","setCurrentTruck");
        this.reset();
        this.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.setDataSource(app.client_manager.getURLByTrackId(track_id));

        this.track_id = track_id;
    }

    public synchronized void getReady(int track_id, int offset)throws IOException {
        this.offset = offset;
        Log.e("MediaManager", "before prepare async");
        if (is_prepared && this.track_id == track_id) {
            Log.d("GET_READY","im ready send");
            app.client_manager.sendReady(track_id);
        }
        else if (!preparing || this.track_id != track_id) {
            Log.e("MediaManager", "inside if in get ready");
            setCurrentTrack(track_id);
            preparing = true;
            is_prepared = false;
            Log.e("MediaManager", "before calling prepare async");
            /*try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            this.prepareAsync();
            Log.e("MediaManager", "after calling prepare async");
        }
        Log.e("MediaManager", "finish get ready");

    }

    public synchronized void play(int track_id, int offset) throws IOException {
        if(this.offset != offset) {
            Log.d("OFFSET",""+offset);
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
