package com.vibeat.vibeatapp.Activities;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.vibeat.vibeatapp.ListClasses.PlaylistRecyclerView;
import com.vibeat.vibeatapp.ListHelpers.RecyclerTouchHelper;
import com.vibeat.vibeatapp.ListHelpers.RecyclerTouchHelperListener;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;

public class PlaylistActivity extends AppCompatActivity implements RecyclerTouchHelperListener{

    private MyApplication app;
    private RecyclerView recyclerView;
    private PlaylistRecyclerView adapter;
    private ItemTouchHelper itemTouchHelper;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        app = (MyApplication) this.getApplication();

        recyclerView = (RecyclerView)findViewById(R.id.playlist);
        if (app.client_manager.party != null && app.client_manager.party.playlist!= null){
            adapter = new PlaylistRecyclerView(PlaylistActivity.this, app.client_manager.party.playlist, recyclerView);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager (this);
            recyclerView.setLayoutManager (mLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);

            ItemTouchHelper.Callback callback = new RecyclerTouchHelper(adapter, PlaylistActivity.this, app);
            itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

            app.gui_manager.changeActivity(PlaylistActivity.this,adapter);
            app.gui_manager.initPlaylistActivity();
        }
        else {
            app.gui_manager.changeActivity(PlaylistActivity.this, (RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder>) null);
            app.gui_manager.switchActivity(EnterPartyActivity.class);
        }

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        int track_id = app.client_manager.party.playlist.tracks.get(position).track_id;
        if(track_id != -1)
            itemTouchHelper.startSwipe(viewHolder);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onDrag(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if(app.client_manager.party.playlist.tracks.get(position).track_id != -1)
            itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onBackPressed(){
        app.client_manager.leaveParty();
        Intent intent = new Intent(this, EnterPartyActivity.class);
        startActivity(intent);
    }
}