package com.vibeat.vibeatapp.ListClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.imageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistRecyclerView extends RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder> {

    private Context context;
    private Playlist playlist;
    private MyApplication app;
    private RecyclerView recyclerView;

    public PlaylistRecyclerView(Context context, Playlist playlist, RecyclerView recyclerView) {
        this.context = context;
        this.playlist = playlist;
        this.app = (MyApplication) (((Activity) context).getApplication());
        this.recyclerView = recyclerView;
    }

    @Override
    public playlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_songs, parent,false);
        return new playlistViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final playlistViewHolder holder, int position) {
        Track track = this.playlist.tracks.get(position);
        holder.title.setText(track.title);
        holder.artist.setText(track.artist);

        List<String> img_paths = new ArrayList<String>();
        List<ImageView> views = new ArrayList<ImageView>();
        img_paths.add(track.img_path);
        views.add(holder.img);
        Log.d("ImagePath","berore");
        imageLoader.loadImageSquare((Activity)context, img_paths, views);
        Log.d("ImagePath","after");

        if(this.playlist.cur_track == position)
            holder.background.setBackgroundColor(R.color.colorPrimaryDark);
        else
            holder.background.setBackgroundColor(Color.TRANSPARENT);

    }

    @Override
    public int getItemCount() {
        return playlist.tracks.size();
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(playlist.tracks, fromPosition, toPosition);
        //app.client_manager.swapTrack(fromPosition,toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;

    }

    public void onItemDismiss(int position) {
        /*if(app.client_manager.party.playlist.cur_track == position)
            app.client_manager.nextSong();*/
        app.client_manager.removeTrack(position);
        playlist.tracks.remove(position);
        notifyItemRemoved(position);
    }

    @SuppressLint("ResourceAsColor")
    public void setCurTrackBackground(int position_old, int position_new){
        PlaylistRecyclerView.playlistViewHolder viewHolder_new, viewHolder_old;
        viewHolder_old = (PlaylistRecyclerView.playlistViewHolder) recyclerView.findViewHolderForAdapterPosition(position_old);
        viewHolder_new = (PlaylistRecyclerView.playlistViewHolder) recyclerView.findViewHolderForAdapterPosition(position_new);
        viewHolder_old.background.setBackgroundColor(Color.TRANSPARENT);
        viewHolder_new.background.setBackgroundColor(R.color.colorPrimaryDark);
        notifyItemChanged(position_old);
        notifyItemChanged(position_new);
    }

    public class playlistViewHolder extends RecyclerView.ViewHolder{

        public TextView title, artist;
        public ImageView img;
        public LinearLayout background;

        public playlistViewHolder(View v){
            super(v);
            this.img = (ImageView) v.findViewById(R.id.imageSong);
            this.title = (TextView) v.findViewById(R.id.title);
            this.artist = (TextView) v.findViewById(R.id.artist);
            this.background = (LinearLayout) v.findViewById(R.id.background);
        }

        @SuppressLint("ResourceAsColor")
        public void onItemClear() {
            itemView.setBackgroundColor(Color.TRANSPARENT);
        }

    }
}
