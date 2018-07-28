package com.vibeat.vibeatapp.ChangeObjects;

import com.vibeat.vibeatapp.Objects.Playlist;

public class DeleteChange extends PlaylistChange{

    int id;

    public DeleteChange(int id) {
        this.id = id;
    }

    @Override
    public void applyChange(Playlist playlist) {
        int pos = playlist.searchTrack(id);
        if(pos>=0 && pos < playlist.tracks.size())
            playlist.tracks.remove(playlist.searchTrack(id));
    }

}
