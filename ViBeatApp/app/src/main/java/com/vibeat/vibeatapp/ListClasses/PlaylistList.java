package com.vibeat.vibeatapp.ListClasses;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vibeat.vibeatapp.Activities.AddMusicActivity;
import com.vibeat.vibeatapp.Activities.CreatePartyActivity;
import com.vibeat.vibeatapp.Activities.PlaylistActivity;
import com.vibeat.vibeatapp.HelperClasses.MediaPlayerManager;
import com.vibeat.vibeatapp.HelperClasses.pictureChange;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.R;

public class PlaylistList implements ListAdapterable {

    public Playlist playlist;

    public PlaylistList(Playlist playlist){
        this.playlist = playlist;
    }

    @Override
    public View initRow(Adapter adapter, Activity context, View v, final int position) {

        final MyApplication app = (MyApplication) context.getApplication();
        final Activity activity = context;
        final Track track = playlist.tracks.get(position);
        final Adapter adap = adapter;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (activity instanceof CreatePartyActivity){
                    app.client_manager.createParty();
                    app.client_manager.party.request.add(new User("Idan Cohen",
                            "/storage/emulated/0/ViBeat/idan.jpg", 2));
                    app.client_manager.addTrack(track);
                    app.media_manager.updatePlaylist(app.client_manager.party.playlist);
                    Intent intent = new Intent(activity, PlaylistActivity.class);
                    activity.startActivity(intent);
                }
                else if( activity instanceof AddMusicActivity) {
                    app.client_manager.addTrack(track);
                    Intent intent = new Intent(activity, PlaylistActivity.class);
                    activity.startActivity(intent);
                }
            }
        });

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
