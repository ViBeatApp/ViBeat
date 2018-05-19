package com.vibeat.vibeatapp.ListClasses;

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

import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.R;

public class PlaylistRecyclerView extends RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder> {

    private Context context;
    private Playlist playlist;

    public PlaylistRecyclerView(Context context, Playlist playlist) {
        this.context = context;
        this.playlist = playlist;
    }

    @Override
    public playlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_songs, parent,false);
        return new playlistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(playlistViewHolder holder, int position) {
        Track track = this.playlist.tracks.get(position);
        holder.title.setText(track.title);
        holder.artist.setText(track.artist);
        Bitmap bm = BitmapFactory.decodeFile(track.img_path);
        holder.img.setImageBitmap(bm);
    }

    @Override
    public int getItemCount() {
        return playlist.tracks.size();
    }

    public void removeTrack(int position) {
        playlist.tracks.remove(position);
        notifyItemRemoved(position);
    }

    public class playlistViewHolder extends RecyclerView.ViewHolder {

        public TextView title, artist;
        public ImageView img;
        public LinearLayout foreground;

        public playlistViewHolder(View v){
            super(v);
            this.img = (ImageView) v.findViewById(R.id.imageSong);
            this.title = (TextView) v.findViewById(R.id.title);
            this.artist = (TextView) v.findViewById(R.id.artist);
            this.foreground = (LinearLayout) v.findViewById(R.id.foreground);
        }


    }
}