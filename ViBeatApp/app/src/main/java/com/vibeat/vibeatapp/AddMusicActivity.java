package com.vibeat.vibeatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.ListView;

public class AddMusicActivity extends AppCompatActivity {

    passingInfo info;

    Track song0 = new Track(0, "Haverot Shelach", "Omer Adam", R.drawable.omeradam, false);
    Track song1 = new Track(1, "Toy", "Neta Barzilai", R.drawable.netabrazilai, false);
    Track song2 = new Track(2, "Ratzity", "Eden Ben Zaken", R.drawable.edenbenzaken, false);
    Track song3 = new Track(3, "Up&Up", "Coldplay", R.drawable.coldplay, false);
    Track song4 = new Track(4, "Olay Nedaber", "Nadav Guedj", R.drawable.nadavguedj, false);
    Track[] songs = {song0, song1, song2, song3, song4};
    Track[] no_songs = {};

    ListView listOfSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);

        Intent i = getIntent();
        info = (passingInfo)i.getSerializableExtra("info");

        final SearchView search_bar = (SearchView) findViewById(R.id.search);
        //search_bar.setQuery("Search for your beat",false);
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listOfSongs.setAdapter(new CostumAdapter(AddMusicActivity.this, songs, info));
                search_bar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        listOfSongs = (ListView) findViewById(R.id.songlist);
        listOfSongs.setAdapter(new CostumAdapter(AddMusicActivity.this, no_songs, info));

    }
}
