package com.vibeat.vibeatapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Adapter;
import android.widget.ListView;

import com.vibeat.vibeatapp.ListHelpers.CostumeListAdapter;
import com.vibeat.vibeatapp.ListClasses.PlaylistList;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.R;

import java.util.ArrayList;
import java.util.List;

public class AddMusicActivity extends AppCompatActivity {
    /*
    Track song0 = new Track(0, "Haverot Shelach", "Omer Adam", R.drawable.omeradam, false);
    Track song1 = new Track(1, "Toy", "Neta Barzilai", R.drawable.netabrazilai, false);
    Track song2 = new Track(2, "Ratzity", "Eden Ben Zaken", R.drawable.edenbenzaken, false);
    Track song3 = new Track(3, "Up&Up", "Coldplay", R.drawable.coldplay, false);
    Track song4 = new Track(4, "Olay Nedaber", "Nadav Guedj", R.drawable.nadavguedj, false);
    Track[] songs = {song0, song1, song2, song3, song4};
    Track[] no_songs = {};*/

    ListView listOfSongs;
    MyApplication app;
    Playlist search_res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);

        app = (MyApplication) this.getApplication();

        final PlaylistList disp_res = new PlaylistList(app.client_manager.searchTracks(""));
        final CostumeListAdapter adapter = new CostumeListAdapter(AddMusicActivity.this,
                disp_res);

        listOfSongs = (ListView) findViewById(R.id.songlist);
        listOfSongs.setAdapter(adapter);

        List<Adapter> l = new ArrayList<Adapter>();
        l.add(adapter);
        app.gui_manager.changeActivity(AddMusicActivity.this, l);
        app.gui_manager.initAddMusicActivity();

    }
}
