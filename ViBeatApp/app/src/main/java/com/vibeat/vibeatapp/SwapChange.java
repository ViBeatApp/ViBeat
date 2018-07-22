package com.vibeat.vibeatapp;

import com.vibeat.vibeatapp.Objects.Playlist;

import java.util.Collections;

public class SwapChange extends PlaylistChange  {

    int id1;
    int id2;

    public SwapChange(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public void applyChange(Playlist playlist) {
        Collections.swap(playlist.tracks,playlist.searchTrack(id1),playlist.searchTrack(id2));
    }
}
