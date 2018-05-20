package com.vibeat.vibeatapp.Activities;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vibeat.vibeatapp.HelperClasses.RecyclerTouchHelper;
import com.vibeat.vibeatapp.HelperClasses.RecyclerTouchHelperListener;
import com.vibeat.vibeatapp.ListClasses.PlaylistRecyclerView;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.R;

public class PlaylistActivity extends AppCompatActivity implements RecyclerTouchHelperListener {

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
        adapter = new PlaylistRecyclerView(PlaylistActivity.this, app.client_manager.party.playlist);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager (this);
        recyclerView.setLayoutManager (mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new RecyclerTouchHelper(adapter, PlaylistActivity.this);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        User user = app.client_manager.user;
        ImageView user_img = (ImageView) findViewById(R.id.this_user);
        TextView user_name = (TextView) findViewById(R.id.hello_user);

        try{
            //URL newurl = new URL(user.img_path);
            //Bitmap bm = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
            //Bitmap bm = BitmapFactory.decodeFile(user.img_path);
            //bm = pictureChange.getCroppedBitmap(bm);
            //user_img.setImageBitmap(bm);.
            user_img.setImageURI(Uri.parse(user.img_path));
        }
        //catch (IOException e){

        //}
        catch (Exception e){}

        user_name.setText("Hi, "+user.name);

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
                        play_pause.setImageResource(R.drawable.ic_pause);
                    else
                        play_pause.setImageResource(R.drawable.ic_play);
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    app.client_manager.nextSong();
                    adapter.notifyDataSetChanged();
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
                    mute.setImageResource(R.drawable.ic_mute);
                }
                else {
                    app.media_manager.mute();
                    mute.setImageResource(R.drawable.ic_unmute);
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onDrag(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder.getAdapterPosition() != app.client_manager.party.playlist.cur_track)
            itemTouchHelper.startDrag(viewHolder);
    }
}