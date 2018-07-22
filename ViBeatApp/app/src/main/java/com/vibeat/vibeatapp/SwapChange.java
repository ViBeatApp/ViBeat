package com.vibeat.vibeatapp;

import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;

import java.util.Collections;
import java.util.List;

public class SwapChange extends PlaylistChange  {

    int id1;
    int id2;

    public SwapChange(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public void applyChange(Playlist playlist) {
        List<Track> songs = playlist.tracks;
        int prev_cur_track_id = playlist.tracks.get(playlist.cur_track).track_id;

        int firstIndex = -1;
        int secondIndex = -1;
        for(int i = 0; i < songs.size(); ++i) {
            if(songs.get(i).track_id == id1) {
                firstIndex = i;
            }
            if(songs.get(i).track_id == id2) {
                secondIndex = i;
            }
        }
        if(firstIndex == -1 || secondIndex == -1)
            return;
        if(secondIndex < firstIndex) {
            Track end = songs.get(firstIndex);
            for(int i = firstIndex; i > secondIndex; i--)
            {
                songs.set(i,songs.get(i-1));
            }
            songs.set(secondIndex, end);
        }
        else if(firstIndex < secondIndex) {
            Track start = songs.get(firstIndex);
            for(int i = firstIndex; i < secondIndex; i++)
            {
                songs.set(i,songs.get(i+1));
            }
            songs.set(secondIndex, start);
        }

        for (int i = 0; i < songs.size() ; i++) {
            if (songs.get(i).track_id == prev_cur_track_id) {
                playlist.cur_track = i;
                break;
            }
        }
    }
}
