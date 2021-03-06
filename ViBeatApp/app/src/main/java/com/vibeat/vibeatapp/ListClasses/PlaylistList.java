package com.vibeat.vibeatapp.ListClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeat.vibeatapp.ListHelpers.ListAdapterable;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.HelperClasses.imageLoader;

import java.util.ArrayList;
import java.util.List;

public class PlaylistList implements ListAdapterable {

    public Playlist playlist;

    public PlaylistList(Playlist playlist){
        this.playlist = playlist;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View initRow(Adapter adapter, Activity context, View v, final int position) {

        final MyApplication app = (MyApplication) context.getApplication();
        final Activity activity = context;
        final Track track = playlist.tracks.get(position);
        final Adapter adap = adapter;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                app.gui_manager.songChosen(track);
            }
        });

        ImageView img = (ImageView) v.findViewById(R.id.imageSong);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView artist = (TextView) v.findViewById(R.id.artist);

        List<String> img_paths = new ArrayList<String>();
        List<ImageView> views = new ArrayList<ImageView>();
        img_paths.add(track.img_path);
        views.add(img);
        Log.d("ImagePath","berore");
        imageLoader.loadImageSquare(activity, img_paths, views);
        Log.d("ImagePath","after");

        title.setText(track.title);
        artist.setText(track.artist+"  ");

        return v;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_of_songs;
    }

    @Override
    public int getCount() {
        return playlist.tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return playlist.tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
