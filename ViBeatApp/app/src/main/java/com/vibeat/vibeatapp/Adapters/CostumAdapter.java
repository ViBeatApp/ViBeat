package com.vibeat.vibeatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeat.vibeatapp.Activities.AddMusicActivity;
import com.vibeat.vibeatapp.Activities.CreatePartyActivity;
import com.vibeat.vibeatapp.Activities.PlaylistActivity;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.HelperClasses.passingInfo;
import com.vibeat.vibeatapp.R;

public class CostumAdapter extends BaseAdapter {

    static LayoutInflater inflater = null;

    Context context;
    Track songs[];
    passingInfo info;

    View selected_row = null;

    public CostumAdapter(Context context, Track songs[], passingInfo info){
        this.context = context;
        this.songs = songs;
        this.info = info;
    }

    @Override
    public int getCount() {
        return songs.length;
    }

    @Override
    public Object getItem(int position) {
        return songs[position];
    }

    @Override
    public long getItemId(int position) {
        return songs[position].track_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_of_songs, null);
        }

        final int ind = position;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof CreatePartyActivity || context instanceof AddMusicActivity)
                    create_handler(v, context, ind);
                else {
                    playlist_handler(v, ind);
                }
            }
        });

        ImageView img = (ImageView) row.findViewById(R.id.imageSong);
        TextView title = (TextView) row.findViewById(R.id.title);
        TextView artist = (TextView) row.findViewById(R.id.artist);

        img.setImageResource(songs[position].icon_id);
        title.setText(songs[position].title);
        artist.setText(songs[position].artist);

        if (songs[position].is_playing){
            row.setBackgroundColor(Color.parseColor("#ff9c40"));
            this.selected_row = row;
        }

        return row;
    }

    private void create_handler(View v, Context c, int position) {
        Intent intent = new Intent(c, PlaylistActivity.class);
        //songs[position].is_playing = true;
        info.chosen = position;
        this.selected_row = v;
        intent.putExtra("info",info);
        c.startActivity(intent);
    }

    private void playlist_handler(View v, int position) {
        for (Track elem : songs) {
            elem.is_playing = false;
            //View tmp = v.findViewById(R.layout.list_of_songs);
        }
        songs[position].is_playing = true;
        info.chosen = position;
        v.setBackgroundColor(Color.parseColor("#ff9c40"));
        this.selected_row.setBackgroundColor(Color.TRANSPARENT);
        this.selected_row = v;
        v.invalidate();
    }
}
