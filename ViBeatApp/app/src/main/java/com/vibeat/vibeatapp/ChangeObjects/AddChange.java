package com.vibeat.vibeatapp.ChangeObjects;

import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;

public class AddChange extends PlaylistChange {

    Track t;

    public AddChange(Track t) {
        this.t = t;
    }

    @Override
    public void applyChange(Playlist playlist) {
        playlist.tracks.add(t);
    }

}
