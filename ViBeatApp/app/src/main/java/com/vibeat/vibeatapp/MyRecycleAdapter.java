package com.vibeat.vibeatapp;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.vibeat.vibeatapp.Objects.Track;

import java.util.ArrayList;
import java.util.List;

public class MyRecycleAdapter extends RecyclerView.Adapter {
    protected List<Track> items = new ArrayList<>();

    public void updateItems(List<Track> newItems) {
        List<Track> oldItems = new ArrayList<>(items);
        DiffUtil.DiffResult diffResult =
                DiffUtil.calculateDiff(new DiffCb(oldItems, newItems));
        items.clear();
        items.addAll(newItems);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Track> getItems(){
        return items;
    }
}
