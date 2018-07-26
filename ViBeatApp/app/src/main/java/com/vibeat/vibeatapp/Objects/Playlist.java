package com.vibeat.vibeatapp.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist{

    public List<Track> tracks;
    public boolean is_playing;
    public int cur_track;

    public Playlist(){
        is_playing = false;
        cur_track = -1;
        tracks = Collections.synchronizedList(new ArrayList<Track>());
    }

    public Playlist(List<Track> tracks, boolean is_playing, int cur_track){
        this.is_playing = is_playing;
        this.cur_track = cur_track;
        this.tracks = tracks;
    }

    public void addTrack(Track t){
        tracks.add(t);
        if (tracks.size() == 1)
            cur_track = 0;
    }

    public int searchTrack(int play_track_id) {
        for (int i = 0; i < this.tracks.size(); i++ ) {
            if(tracks.get(i).track_id == play_track_id)
                return i;
        }
        return -1;
    }
}
