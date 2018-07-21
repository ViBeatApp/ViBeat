package com.vibeat.vibeatapp.HelperClasses;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.ServerSide.userIntention;

import java.io.IOException;

public class MyMediaPlayer extends MediaPlayer {

    MyApplication app;
    public int track_id = -1;
    public boolean is_mute = false;
    public boolean preparing = false;
    public boolean is_prepared = false;
    public int offset = 0;
    //public int index;
    public boolean startAfterSeek = false;
    public boolean joiningPlayingParty = false;

    public MyMediaPlayer(final MyApplication app, final int index){
        super();
        this.app = app;
       // this.index = index;

        this.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
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
                    app.client_manager.nextSong(userIntention.ON_COMPLETION);
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
                Log.e("Tomer", "after seek");
                if(startAfterSeek) {
                    Log.e("DebugMediaPlayer", "startAfterSeek");
                    if(!mp.isPlaying())
                        start();
                    startAfterSeek = false;
                    return;
                }

                if (isCurTrack()) {
                    Log.e("DebugMediaPlayer", "not !!!! startAfterSeek");
                    if(joiningPlayingParty){
                        start();
                        mp.setVolume(0,0);
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        Log.e("DebugMediaPlayer", "waitingTimer");
                                        app.client_manager.sendReady(track_id);
                                    }
                                },
                                4500);
                    }
                    else {
                        Log.e("DebugMediaPlayer", "before sending I'm ready");
                        app.client_manager.sendReady(track_id);
                    }
                }
            }
        });

        this.unmute();
    }

    private synchronized void whenPrepared() {
        Log.e("Test1", "inside when prepared");
        if(app.client_manager.party != null) {
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

    public synchronized void getReady(int track_id, int offset, boolean joiningPlayingParty)throws IOException {
        int old_offset = this.offset;
        this.offset = offset;
        this.joiningPlayingParty = joiningPlayingParty;
        //if(this.isPlaying()) this.pause();
        if (is_prepared && this.track_id == track_id){
            Log.e("DebugMediaPlayer", "internal-offset: " + old_offset + " new-offset: " + offset);
                Log.e("DebugMediaPlayer", "false - mp need to prepare");
                this.startAfterSeek = false;
                this.seekTo(offset);
        }
        else if (!preparing || this.track_id != track_id) {

            Log.e("DebugMediaPlayer", "correct");
            setCurrentTrack(track_id);
            preparing = true;
            is_prepared = false;
            this.prepareAsync();
        }
        Log.e("Test1", "finish get ready");

    }

    public synchronized void play(int track_id, int offset) throws IOException {
        Log.d("burger","inside play");
        if(is_mute == false)
            this.setVolume(1,1);

        if(!isCurTrack())
            Log.d("burger","ERROR");

        if(this.offset == offset) {
            Log.d("burger","Play - SameOffset");
            this.start();
            app.gui_manager.startProgressBar(this, this.offset);
        }

        else if(this.offset != offset) {
            Log.d("burger","offset = "+offset);
            this.offset = offset;
            this.startAfterSeek = true;
            this.joiningPlayingParty = false;
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
        return app.client_manager.getTrackPosFromId(this.track_id) == app.client_manager.party.playlist.cur_track;
    }

}
