package com.vibeat.vibeatapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ListView;

import com.vibeat.vibeatapp.ListHelpers.CostumeListAdapter;
import com.vibeat.vibeatapp.ListClasses.PlaylistList;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.R;

import java.util.ArrayList;
import java.util.List;

public class CreatePartyActivity extends AppCompatActivity {

    ListView listOfSongs;
    MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_party);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        app = (MyApplication) this.getApplication();
        final Party party = new Party(app.client_manager.user,
                                app.client_manager.user.name+"'s Party",
                                true, -1);
        app.client_manager.party = party;
        Playlist search_res = app.client_manager.searchTracks("");

        listOfSongs = (ListView) findViewById(R.id.list);
        CostumeListAdapter adap = new CostumeListAdapter(CreatePartyActivity.this,
                new PlaylistList(search_res));
        listOfSongs.setAdapter(adap);

        List<Adapter> l = new ArrayList<Adapter>();
        l.add(adap);
        app.gui_manager.changeActivity(CreatePartyActivity.this, l);
        app.gui_manager.initCreatePartyActivity();
    }
}