package com.example.dana.appdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class createPartyActivity extends AppCompatActivity {

    ListView listOfSongs;

    Track song0 = new Track(0, "Haverot Shelach", "Omer Adam", R.drawable.omeradam, false);
    Track song1 = new Track(1, "Toy", "Neta Barzilai", R.drawable.netabrazilai, false);
    Track song2 = new Track(2, "Ratzity", "Eden Ben Zaken", R.drawable.edenbenzaken, false);
    Track song3 = new Track(3, "Up&Up", "Coldplay", R.drawable.coldplay, false);
    Track song4 = new Track(4, "Olay Nedaber", "Nadav Guedj", R.drawable.nadavguedj, false);
    Track songs[] = {song0, song1, song2, song3, song4};

    passingInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_party);

        Intent i = getIntent();
        info = (passingInfo)i.getSerializableExtra("info");

        listOfSongs = (ListView) findViewById(R.id.list);
        listOfSongs.setAdapter(new CostumAdapter(createPartyActivity.this, songs, info));

        EditText partyName = (EditText) findViewById(R.id.editText);
        partyName.setText(info.user_name+"'s Party");

        Switch isPrivate = (Switch) findViewById(R.id.isPrivate);
        isPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                Switch isPrivate = (Switch) buttonView;
                if (isChecked)
                    isPrivate.setText("Private");
                else
                    isPrivate.setText("Public");
            }
        });
    }
}