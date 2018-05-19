package com.vibeat.vibeatapp.ListClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeat.vibeatapp.Activities.AddMusicActivity;
import com.vibeat.vibeatapp.Activities.CreatePartyActivity;
import com.vibeat.vibeatapp.Activities.PlaylistActivity;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.R;

import java.util.List;


public class PlayListSwipeList extends ArrayAdapter{

    Playlist playlist;

    public PlayListSwipeList(Context context, Playlist playlist) {
        super(context,0, playlist.tracks);
        this.playlist = playlist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        Track track = (Track) getItem(position);

        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.list_of_songs, parent, false);

        ImageView img = (ImageView) v.findViewById(R.id.imageSong);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView artist = (TextView) v.findViewById(R.id.artist);

        Bitmap bm1 = BitmapFactory.decodeFile(track.img_path);
        img.setImageBitmap(bm1);

        title.setText(track.title);
        artist.setText(track.artist);

        if (position == playlist.cur_track)
            v.setBackgroundColor(Color.parseColor("#ff9c40"));
        else
            v.setBackgroundColor(Color.TRANSPARENT);

        return v;
    }
}
