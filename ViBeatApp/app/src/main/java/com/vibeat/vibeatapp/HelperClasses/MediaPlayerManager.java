package com.vibeat.vibeatapp.HelperClasses;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.vibeat.vibeatapp.Objects.Playlist;

import java.io.IOException;

public class MediaPlayerManager {

    public Playlist playlist;
    public MediaPlayer mediaPlayer1;
    public MediaPlayer mediaPlayer2;
    public boolean prepared_current;
    public boolean prepared_next;
    public boolean isMute;
    public int playing;

    public MediaPlayerManager(Playlist playlist){
        this();
        this.playlist = playlist;
    }

    public MediaPlayerManager(){
        this.mediaPlayer1 = new MediaPlayer();
        mediaPlayer1.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.mediaPlayer2 = new MediaPlayer();
        mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);

        this.prepared_current = false;
        this.prepared_next = false;
        this.isMute = false;
        this.playing = 1;
    }

    public void updatePlaylist(Playlist playlist){
        this.playlist = playlist;
    }

    //start playing at the start of the cur_track;
    public void play()throws IOException{
        if(!this.prepared_current)
            prepareSong();
        MediaPlayer mediaPlayer = (this.playing == 1) ? mediaPlayer1 : mediaPlayer2;
        play(mediaPlayer.getCurrentPosition());
    }

    //start playing at the offset in the cur_track;
    public void play(int offset){
        MediaPlayer mediaPlayer = (this.playing == 1) ? mediaPlayer1 : mediaPlayer2;
        mediaPlayer.start();
        mediaPlayer.seekTo(offset);
    }

    public void pause(){
        MediaPlayer mediaPlayer = (this.playing == 1) ? mediaPlayer1 : mediaPlayer2;
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    //mute the player
    public void mute(){
        this.isMute = true;
        mediaPlayer1.setVolume(0,0);
        mediaPlayer2.setVolume(0,0);
    }

    //unmute the player
    public void unmute(){
        this.isMute = false;
        mediaPlayer1.setVolume(1,1);
        mediaPlayer2.setVolume(1,1);
    }

    public void prepareSong(int index, MediaPlayer mediaPlayer) throws IOException{
        //String url = "http://docs.google.com/uc?/export=download&id=1qL3nOMI0CwaaNsWqPrDC1VuOEHNK7Sh4"; // your URL here
        mediaPlayer.reset();
        String url = this.playlist.tracks.get(index).track_path;
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare(); // might take long! (for buffering, etc)
    }

    public void prepareSong() throws IOException{
        if (!this.prepared_current) {
            MediaPlayer mediaPlayer = (this.playing == 1) ? mediaPlayer1 : mediaPlayer2;
            prepareSong(playlist.cur_track, mediaPlayer);
            this.prepared_current = true;
        }
        if (!this.prepared_next){
            prepareNextSong();
        }
    }

    public void prepareNextSong() throws IOException{
        if(!this.prepared_next) {
            if (this.playlist.cur_track + 1 < this.playlist.tracks.size()) {
                MediaPlayer mediaPlayer = (this.playing == 1) ? mediaPlayer2 : mediaPlayer1;
                prepareSong(playlist.cur_track + 1, mediaPlayer);
            }
            this.prepared_next = true;
        }
    }

    public void playNext()throws IOException{
        MediaPlayer mediaPlayer = (this.playing == 1) ? mediaPlayer1 : mediaPlayer2;
        mediaPlayer.reset();

        playing = 3 - playing;
        this.prepared_current = this.prepared_next;
        this.prepared_next = false;
        play();
        playlist.cur_track++;
        prepareNextSong();
    }

    public void resetPlaylist(){
        this.playlist = null;
        this.mediaPlayer1.reset();
        this.mediaPlayer2.reset();
        this.playing = 1;
        unmute();
        this.prepared_current = false;
        this.prepared_next = false;
    }

    public int getOffset(){
        MediaPlayer mediaPlayer = (this.playing == 1) ? mediaPlayer1 : mediaPlayer2;
        return mediaPlayer.getCurrentPosition();
    }

}
