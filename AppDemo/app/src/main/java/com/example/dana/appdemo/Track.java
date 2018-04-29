package com.example.dana.appdemo;

public class Track {

    public int song_id;
    public String title;
    public String artist;
    public int icon_id;
    public boolean is_playing;

    public Track(int song_id, String title, String artist, int icon_id, boolean is_playing) {
        this.song_id = song_id;
        this.title = title;
        this.artist = artist;
        this.icon_id = icon_id;
        this.is_playing = is_playing;
    }
}
