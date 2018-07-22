package com.vibeat.vibeatapp;

import com.vibeat.vibeatapp.Objects.Playlist;

public class DeleteChange extends PlaylistChange{

    int id;

    public DeleteChange(int id) {
        this.id = id;
    }

    @Override
    public void applyChange(Playlist playlist) {
        playlist.tracks.remove(playlist.searchTrack(id));
    }

}
