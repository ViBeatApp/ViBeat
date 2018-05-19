package com.vibeat.vibeatapp.Activities;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.vibeat.vibeatapp.HelperClasses.RecyclerTouchHelper;
import com.vibeat.vibeatapp.HelperClasses.RecyclerTouchHelperListener;
import com.vibeat.vibeatapp.ListClasses.CostumeListAdapter;
import com.vibeat.vibeatapp.ListClasses.PlayListSwipeList;
import com.vibeat.vibeatapp.ListClasses.PlaylistList;
import com.vibeat.vibeatapp.ListClasses.PlaylistRecyclerView;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;

import static com.vibeat.vibeatapp.R.mipmap.ic_launcher_round;

public class PlaylistActivity extends AppCompatActivity implements RecyclerTouchHelperListener {

    /*Track song0 = new Track(0, "Haverot Shelach", "Omer Adam", R.drawable.omeradam, false);
    Track song1 = new Track(1, "Toy", "Neta Barzilai", R.drawable.netabrazilai, false);
    Track song2 = new Track(2, "Ratzity", "Eden Ben Zaken", R.drawable.edenbenzaken, false);
    Track song3 = new Track(3, "Up&Up", "Coldplay", R.drawable.coldplay, false);
    Track song4 = new Track(4, "Olay Nedaber", "Nadav Guedj", R.drawable.nadavguedj, false);
    Track[] songs = {song0, song1, song2, song3, song4};
    Track[] shown = {song0,song1};
*/
    private ListView listOfSongs;
    private MyApplication app;
    private SwipeMenuListView swipeList;
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
        adapter = new PlaylistRecyclerView(PlaylistActivity.this, app.client_manager.party.playlist);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager (this);
        recyclerView.setLayoutManager (mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new RecyclerTouchHelper(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        final ImageButton mute = (ImageButton) findViewById(R.id.mute);

        // admin only
        final ImageButton play_pause = (ImageButton) findViewById(R.id.play_pause);
        ImageButton next = (ImageButton) findViewById(R.id.next);
        ImageButton connected = (ImageButton) findViewById(R.id.connected);
        ImageButton add = (ImageButton) findViewById(R.id.add);

        if (app.client_manager.is_admin){
            connected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PlaylistActivity.this, ConnectedActivity.class);
                    startActivity(intent);
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PlaylistActivity.this, AddMusicActivity.class);
                    startActivity(intent);
                }
            });

            play_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    app.client_manager.commandPlayPause();
                    if(app.client_manager.party.playlist.is_playing)
                        play_pause.setImageResource(R.drawable.pause);
                    else
                        play_pause.setImageResource(R.drawable.play);
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    app.client_manager.nextSong();
                    //((BaseAdapter) adap).notifyDataSetChanged();
                }
            });


        }
        else {
            connected.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
            play_pause.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        }

        mute.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(app.media_manager.isMute) {
                    app.media_manager.unmute();
                    mute.setImageResource(R.drawable.mute);
                }
                else {
                    app.media_manager.mute();
                    mute.setImageResource(R.drawable.unmute);
                }
            }
        });

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager.leaveParty();
                app.media_manager.resetPlaylist();
                Intent intent = new Intent(PlaylistActivity.this, EnterPartyActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        itemTouchHelper.startSwipe(viewHolder);
    }

    @Override
    public void onDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }
}