package com.vibeat.vibeatapp;

import android.support.v7.util.DiffUtil.Callback;

import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;

import java.util.List;

public class DiffCb extends Callback {
    List<Track> newItems;
    List<Track> oldItems;

    public DiffCb(List<Track> newItems, List<Track> oldItems) {
        super();
        this.newItems = newItems;
        this.oldItems = oldItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        // Simulate a really long running diff calculation.
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).db_id == newItems.get(newItemPosition).db_id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).db_id == newItems.get(newItemPosition).db_id;
    }
}
