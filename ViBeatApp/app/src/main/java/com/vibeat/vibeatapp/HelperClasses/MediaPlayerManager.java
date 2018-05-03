package com.vibeat.vibeatapp.HelperClasses;

import com.vibeat.vibeatapp.Objects.Playlist;

public class MediaPlayerManager {

    Playlist playlist;

    public MediaPlayerManager(Playlist playlist){
        this.playlist = playlist;
    }

    //start playing at the start of the cur_track;
    public void play(){

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
