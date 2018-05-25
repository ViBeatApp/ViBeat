package com.vibeat.vibeatapp.ListClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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

import java.util.Collections;

public class PlaylistRecyclerView extends RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder> {

    private Context context;
    private Playlist playlist;
    private MyApplication app;

    public PlaylistRecyclerView(Context context, Playlist playlist) {
        this.context = context;
        this.playlist = playlist;
        this.app = (MyApplication) (((Activity) context).getApplication());
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
        Bitmap bm = BitmapFactory.decodeFile(track.img_path);
        holder.img.setImageBitmap(bm);

        if(this.playlist.cur_track == position)
            holder.background.setBackgroundColor(R.color.colorLightStroke);
        else
            holder.background.setBackgroundColor(Color.TRANSPARENT);

    }

    @Override
    public int getItemCount() {
        return playlist.tracks.size();
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        if (toPosition != playlist.cur_track){
            Collections.swap(playlist.tracks, fromPosition, toPosition);
            app.client_manager.swapTrack(fromPosition,toPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
        else
            return false;
    }

    public void onItemDismiss(int position) {
        playlist.tracks.remove(position);
        notifyItemRemoved(position);
    }

    public class playlistViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

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


        @SuppressLint("ResourceAsColor")
        @Override
        public boolean onLongClick(View v) {
            this.background.setBackgroundColor(R.color.colorPrimary);
            notifyDataSetChanged();
            return true;
        }
    }
}
