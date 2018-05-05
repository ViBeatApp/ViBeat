package com.vibeat.vibeatapp.Objects;

import com.vibeat.vibeatapp.ListClasses.ListAdapterable;

import java.util.ArrayList;
import java.util.List;

public class Playlist{

    public List<Track> tracks;
    public boolean is_playing;
    public int cur_track;

    public Playlist(){
        is_playing = false;
        cur_track = -1;
        tracks = new ArrayList<Track>();
    }

    public Playlist(List<Track> tracks, boolean is_playing, int cur_track){
        this.is_playing = is_playing;
        this.cur_track = cur_track;
        this.tracks = tracks;
    }

    public void addTrack(Track t){
        tracks.add(t);
    }
}
