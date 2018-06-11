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
    public int index;
    public boolean startAfterSeek = false;

    public MyMediaPlayer(final MyApplication app, final int index){
        super();
        this.app = app;
        this.index = index;

        this.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e("Test1", "on prepared");
                whenPrepared();
            }
        });

        this.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("Test1", "on complete : media player index = "+index);
                Log.e("Test1", "on complete : track id = "+app.client_manager.party.playlist.tracks.get(
                        app.client_manager.party.playlist.cur_track).track_id);
                if(isCurTrack())
                    app.client_manager.nextSong();
            }
        });

        this.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("Test1", "on error listener: index = "+index);
                return true;
            }
        });

        this.setOnSeekCompleteListener(new OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.e("Test1", "after seek");

                if(startAfterSeek) {
                    Log.e("Test1", "before start");

                    start();
                    startAfterSeek = false;
                    return;
                }
                if (isCurTrack()) {
                    Log.e("Test1", "before sending I'm ready");
                    app.client_manager.sendReady(track_id);
                }

            }
        });

        this.unmute();
    }

    private synchronized void whenPrepared() {
        Log.e("Test1", "inside when prepared");
        if(app.client_manager.party != null) {
            Log.e("Test1", "inside when prepared if statement");
            this.is_prepared = true;
            this.preparing = false;
            this.startAfterSeek = false;
            this.start();
            this.pause();
            this.seekTo(this.offset);
        }
    }

    public synchronized void setCurrentTrack(int track_id)throws IOException {
        Log.d("Test1","setCurrentTruck");
        this.reset();
        this.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.setDataSource(app.client_manager.getURLByTrackId(track_id));

        this.track_id = track_id;
    }

    public synchronized void getReady(int track_id, int offset)throws IOException {
        Log.e("Test1", "inside get ready my media player: index = "+index);
        Log.e("Test1", "got offset = "+offset);
        Log.e("Test1", "media player old offset = "+this.offset);
        this.offset = offset;

        if (is_prepared && this.track_id == track_id){
            Log.e("Test1", "already prepared");
            Log.e("Test1", "changing offset");

            this.startAfterSeek = false;
            this.seekTo(offset);
        }
        else if (!preparing || this.track_id != track_id) {

            Log.e("Test1", "inside if in get ready");
            setCurrentTrack(track_id);
            preparing = true;
            is_prepared = false;
            Log.e("Test1", "before calling prepare async");
            this.prepareAsync();
            Log.e("Test1", "after calling prepare async");
        }
        Log.e("Test1", "finish get ready");

    }

    public synchronized void play(int track_id, int offset) throws IOException {
        Log.d("Test1","inside play");

        if(!isCurTrack())
            Log.d("Test1","ERROR");

        if(this.offset == offset)
            this.start();

        else if(this.offset != offset) {
            Log.d("Test1","offset ="+offset);
            this.offset = offset;
            this.startAfterSeek = true;
            this.seekTo(this.offset);
        }
        else /*if (this.track_id != track_id || !is_prepared) */ {
            Log.d("Test1","Error - playing on wrong media player");
            setCurrentTrack(track_id);
            this.prepare();
            this.is_prepared = true;
            this.offset = offset;
            this.startAfterSeek = true;
            this.seekTo(this.offset);
        }
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
        Log.d("Test1","isCurTrack");
        Log.d("Test1","this track id = "+this.track_id);
        Log.d("Test1","this pos = "+app.client_manager.getTrackPosFromId(this.track_id));
        Log.d("Test1","cur track pos = "+app.client_manager.party.playlist.cur_track);
        return app.client_manager.getTrackPosFromId(this.track_id) == app.client_manager.party.playlist.cur_track;
    }

}
