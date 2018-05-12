package com.vibeat.vibeatapp.HelperClasses;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.vibeat.vibeatapp.Objects.Playlist;

import java.io.IOException;

public class MediaPlayerManager {

    Playlist playlist;

    public MediaPlayerManager(Playlist playlist){
        this.playlist = playlist;
    }
    MediaPlayer mediaPlayer = new MediaPlayer();


    //start playing at the start of the cur_track;
    public void play(){
        try {
            //String url = "http://docs.google.com/uc?/export=download&id=1qL3nOMI0CwaaNsWqPrDC1VuOEHNK7Sh4"; // your URL here
            String url = this.playlist.tracks.get(this.playlist.cur_track).track_path;
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //start playing at the offset in the cur_track;
    public void play(double offset){

    }

    //pause
    public void pause(){

    }

    //mute the player
    public void mute(){

    }

    //check if cur_track has changed and if so start playing at the new one
    public void refreshStatus(){

    }
}
