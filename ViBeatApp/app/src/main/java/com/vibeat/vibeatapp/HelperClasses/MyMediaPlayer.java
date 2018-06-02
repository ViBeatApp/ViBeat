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
    public boolean is_prepared = false;
    public boolean request_ready = false;

    public MyMediaPlayer(final MyApplication app){
        super();
        this.app = app;

        this.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                is_prepared = true;
                request_ready = false;
                Log.e("MediaManager","send ready");
                app.client_manager.sendReady(track_id);
            }
        });

        this.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                app.client_manager.nextSong();
            }
        });
    }

    public void setCurrentTrack(int track_id)throws IOException{
        this.track_id = track_id;
        this.reset();
        this.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.setDataSource(app.client_manager.getURLByTrackId(track_id));
    }

    public void getReady(int track_id, int offset)throws IOException{
        setCurrentTrack(track_id);
        this.request_ready = true;
        Log.e("MediaManager","before prepare async");
        this.prepareAsync();
        Log.e("MediaManager","afer prepare async");
    }

    public void play(int track_id, int offset) throws IOException {
        if (this.track_id != track_id || !is_prepared) {
            setCurrentTrack(track_id);
            this.prepare();
            this.is_prepared = true;
        }
        this.start();
        this.seekTo(offset);
    }

    public void mute(){
        this.setVolume(0, 0 );
        is_mute = true;
    }

    public void unmute(){
        this.setVolume(1, 1 );
        is_mute = false;
    }

}
