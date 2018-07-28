package com.vibeat.vibeatapp.ChangeObjects;

import com.vibeat.vibeatapp.Objects.Playlist;

import java.util.Random;

public abstract class PlaylistChange {
    public int change_id = -1;

    public PlaylistChange() {
        change_id = new Random().nextInt(10000000);
    }

    public abstract void applyChange(Playlist playlist);
}
