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
    public boolean joiningPlayingParty = false;

    public MyMediaPlayer(final MyApplication app, final int index){
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
                    app.client_manager.nextSong(userIntention.ON_COMPLETION);
            }
        });

        this.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        this.setOnSeekCompleteListener(new OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (isCurTrack()) {
                    musicBar();
                    if(joiningPlayingParty){
                        mp.setVolume(0,0);
                        start();
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        app.client_manager.sendReady(track_id);
                                    }
                                },
                                3500);
                    }
                    else {
                        start();
                        pause();
                        app.client_manager.sendReady(track_id);
                    }
                }
            }
        });

        this.unmute();
    }

    private synchronized void whenPrepared() {
        if(app.client_manager.party != null) {
            this.seekTo(this.offset);
            this.is_prepared = true;
            this.preparing = false;
        }
    }

    public synchronized void setCurrentTrack(int track_id)throws IOException {
        this.reset();
        this.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.setDataSource(app.client_manager.getURLByTrackId(track_id));
        this.track_id = track_id;
    }

    public synchronized void getReady(int track_id, int offset, boolean joiningPlayingParty)throws IOException {
        this.offset = offset;
        this.joiningPlayingParty = joiningPlayingParty;
        if (is_prepared && this.track_id == track_id){
            this.seekTo(offset);
        }
        else if (!preparing || this.track_id != track_id) {
            setCurrentTrack(track_id);
            preparing = true;
            is_prepared = false;
            this.prepareAsync();
        }

    }

    public synchronized void play(int track_id, int offset) throws IOException {
        if(is_mute == false)
            this.setVolume(1,1);

        assert (this.offset == offset && isCurTrack());

        if(this.getCurrentPosition() == offset) {
            this.start();
            musicBar();
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

    public void musicBar(){
        if(app.client_manager.isAdmin()) 
            app.gui_manager.startSeekBar(this, this.getCurrentPosition());
        else
            app.gui_manager.startProgressBar(this, this.getCurrentPosition());
    }

}
