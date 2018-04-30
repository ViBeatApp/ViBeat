package com.vibeat.vibeatapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

public class playlistActivity extends AppCompatActivity {

    passingInfo info;

    Track song0 = new Track(0, "Haverot Shelach", "Omer Adam", R.drawable.omeradam, false);
    Track song1 = new Track(1, "Toy", "Neta Barzilai", R.drawable.netabrazilai, false);
    Track song2 = new Track(2, "Ratzity", "Eden Ben Zaken", R.drawable.edenbenzaken, false);
    Track song3 = new Track(3, "Up&Up", "Coldplay", R.drawable.coldplay, false);
    Track song4 = new Track(4, "Olay Nedaber", "Nadav Guedj", R.drawable.nadavguedj, false);
    Track[] songs = {song0, song1, song2, song3, song4};
    Track[] shown = {song0,song1};

    ListView listOfSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Intent i = getIntent();
        info = (passingInfo)i.getSerializableExtra("info");
        songs[info.chosen].is_playing = true;

        if( info.chosen == 0 ) {
            shown = new Track[1];
            shown[0] = song0;
        }

        listOfSongs = (ListView) findViewById(R.id.playlist);
        listOfSongs.setAdapter(new CostumAdapter(playlistActivity.this, shown, info));

        ImageButton connected = (ImageButton) findViewById(R.id.connected);
        if(info.group_id == 100)
            connected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connected_list(v);
            }
        });
        else
            connected.setVisibility(View.GONE);

        ImageButton add = (ImageButton) findViewById(R.id.add);
        if(info.group_id == 100)
            add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_music(v);
            }
        });
        else
            add.setVisibility(View.GONE);

    }

    public void connected_list(View v) {
        Intent intent = new Intent(this, connectedActivity.class);
        intent.putExtra("info",info);
        startActivity(intent);
    }

    public void add_music(View v) {
        Intent intent = new Intent(this, AddMusicActivity.class);
        intent.putExtra("info",info);
        startActivity(intent);
    }
}