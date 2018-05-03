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

    public void addTrack(Track t){
        tracks.add(t);
    }
}
