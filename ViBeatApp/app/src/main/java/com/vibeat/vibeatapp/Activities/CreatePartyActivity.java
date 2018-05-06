package com.vibeat.vibeatapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import com.vibeat.vibeatapp.ListClasses.CostumeListAdapter;
import com.vibeat.vibeatapp.ListClasses.PlaylistList;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.R;

public class CreatePartyActivity extends AppCompatActivity {

    ListView listOfSongs;
    MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_party);

        app = (MyApplication) this.getApplication();
        final Party party = new Party(app.client_manager.user,
                                app.client_manager.user.name+"'s Party",
                                true);
        app.client_manager.party = party;
        Playlist search_res = app.client_manager.searchTracks("");

        listOfSongs = (ListView) findViewById(R.id.list);
        listOfSongs.setAdapter(new CostumeListAdapter(CreatePartyActivity.this,
                new PlaylistList(search_res)));

        EditText partyName = (EditText) findViewById(R.id.editText);
        partyName.setText(party.party_name);

        partyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String party_name = s.toString();
                party.party_name = party_name;
            }
        });

        final ImageView isPrivate = (ImageView) findViewById(R.id.isPrivate);
        if (!app.client_manager.party.is_private)
            isPrivate.setImageResource(R.drawable.public_img2);

        isPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.client_manager.party.is_private) {
                    isPrivate.setImageResource(R.drawable.private_img);
                    app.client_manager.party.is_private = true;
                }
                else {
                    isPrivate.setImageResource(R.drawable.public_img2);
                    app.client_manager.party.is_private = false;
                }
            }
        });
    }
}