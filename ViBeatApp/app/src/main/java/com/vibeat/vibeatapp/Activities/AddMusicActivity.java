package com.vibeat.vibeatapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Adapter;
import android.widget.ListView;

import com.vibeat.vibeatapp.ListClasses.PlaylistList;
import com.vibeat.vibeatapp.ListHelpers.CostumeListAdapter;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.R;

import java.util.ArrayList;
import java.util.List;

public class AddMusicActivity extends AppCompatActivity {

    ListView listOfSongs;
    MyApplication app;
    Playlist search_res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);

        app = (MyApplication) this.getApplication();

        final CostumeListAdapter adapter = new CostumeListAdapter(AddMusicActivity.this,
                new PlaylistList(new Playlist()));
        listOfSongs = (ListView) findViewById(R.id.songlist);
        listOfSongs.setAdapter(adapter);

        List<Adapter> l = new ArrayList<Adapter>();
        l.add(adapter);

        app.gui_manager.changeActivity(AddMusicActivity.this, l);
        app.gui_manager.initAddMusicActivity();
    }
}
