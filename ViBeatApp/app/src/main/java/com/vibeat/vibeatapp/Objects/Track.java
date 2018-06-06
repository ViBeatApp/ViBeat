package com.vibeat.vibeatapp.Objects;

public class Track {

    public String db_id;
    public int track_id;
    public String title;
    public String artist;
    public String img_path;
    public String track_path;

    public Track(String db_id, int track_id, String title, String artist, String img_path, String track_path) {
        this.db_id = db_id;
        this.track_id = track_id;
        this.title = title;
        this.artist = artist;
        this.img_path = img_path;
        this.track_path = track_path;
    }
}
