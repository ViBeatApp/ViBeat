package com.vibeat.vibeatapp.Managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vibeat.vibeatapp.Activities.AddMusicActivity;
import com.vibeat.vibeatapp.Activities.ConnectedActivity;
import com.vibeat.vibeatapp.Activities.CreatePartyActivity;
import com.vibeat.vibeatapp.Activities.EnterPartyActivity;
import com.vibeat.vibeatapp.Activities.LoadingActivity;
import com.vibeat.vibeatapp.Activities.MainActivity;
import com.vibeat.vibeatapp.Activities.NoConnectionActivity;
import com.vibeat.vibeatapp.Activities.PlaylistActivity;
import com.vibeat.vibeatapp.ChangeObjects.GUIChange;
import com.vibeat.vibeatapp.HelperClasses.MyMediaPlayer;
import com.vibeat.vibeatapp.ListClasses.PartiesList;
import com.vibeat.vibeatapp.ListClasses.PlaylistList;
import com.vibeat.vibeatapp.ListClasses.PlaylistRecyclerView;
import com.vibeat.vibeatapp.ListHelpers.CostumeListAdapter;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.ServerSide.partyInfo;
import com.vibeat.vibeatapp.ServerSide.userIntention;
import com.vibeat.vibeatapp.HelperClasses.imageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

public class GUIManager{
    public Activity act;
    List<Adapter> adapters;
    MyApplication app;
    RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder> recycler_adapter;
    public List<GUIChange> cur_changes;
    public Boolean leaveParty = false;
    public Boolean validOffset = true;

    public MyMediaPlayer mediaPlayer;
    public Integer curProgress = 0;
    public boolean isPlaylistOnTop = true;
    public Playlist searchRes = null;

    public GUIManager(Activity act, List<Adapter> adapters){
        this.act = act;
        this.adapters = adapters;
        this.recycler_adapter = null;
        app = (MyApplication) act.getApplication();
        this.cur_changes = new ArrayList<GUIChange>();
        app.semaphoreDisconnected = new Semaphore(0);
        app.semaphoreDisconnected.release();
        Log.d("Test7", "gui builder after create lock");
    }

    public GUIManager(Activity act, RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder> adapter){
        this.act = act;
        this.recycler_adapter = adapter;
        this.adapters = null;
        app = (MyApplication) act.getApplication();
        app.semaphoreDisconnected = new Semaphore(0);
    }

    public void changeActivity(Activity act, List<Adapter> adapters){
        this.act = act;
        this.adapters = adapters;
        app = (MyApplication) act.getApplication();
    }

    public void changeActivity(Activity act, RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder> adapter){
        this.act = act;
        this.recycler_adapter = adapter;
        app = (MyApplication) act.getApplication();
    }

    public void login() {
        if(app.sender_thread != null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    app.client_manager.initLocationTracking(act);
                }
            });
            switchActivity(EnterPartyActivity.class);
        }
    }

    public void requestJoin(partyInfo party) {
        app.client_manager.requested_party = party;
        switchActivity(LoadingActivity.class);
    }

    public void completeJoin(boolean joiningInTheMiddle){
        if(joiningInTheMiddle) {
            if(!(act instanceof LoadingActivity)){
                switchActivity(LoadingActivity.class);
            }
            leaveParty = false;
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            if(!leaveParty && !(act instanceof PlaylistActivity))
                                switchActivity(PlaylistActivity.class);
                        }
                    },
                    4100);
        }
        else
            switchActivity(PlaylistActivity.class);
    }

    public void putPartyResults(List<partyInfo> party_list) {
        if(act instanceof EnterPartyActivity) {
            final CostumeListAdapter adap =(CostumeListAdapter) adapters.get(0);
            ((PartiesList) adap.list_obj).nearby_parties = party_list;
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adap.notifyDataSetChanged();
                    if( adapters.get(0).isEmpty() ){
                        act.findViewById(R.id.no_parties).setVisibility(View.VISIBLE);
                        act.findViewById(R.id.parties_list).setVisibility(View.GONE);
                    }
                    else {
                        act.findViewById(R.id.no_parties).setVisibility(View.GONE);
                        act.findViewById(R.id.parties_list).setVisibility(View.VISIBLE);
                    }

                }
            });

        }
    }

    public void rejected() {
        if( act instanceof LoadingActivity ) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                Toast.makeText(act,
                    "Sorry, your request was not accepted...",
                    Toast.LENGTH_LONG).show();
                }
            });
            if (!(act instanceof EnterPartyActivity)) {
                switchActivity(EnterPartyActivity.class);
            }
        }
    }

    public void songChosen(Track track) {
        if (act instanceof CreatePartyActivity) {
            app.client_manager.createParty();
        }
        app.client_manager.addTrack(track);
        switchActivity(PlaylistActivity.class);
    }

    public void play(int play_track_id) {

        app.client_manager.party.playlist.cur_track = app.client_manager.party.playlist.searchTrack(play_track_id);
        app.client_manager.party.playlist.is_playing = true;
        if (act instanceof PlaylistActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (act instanceof PlaylistActivity) {
                        act.findViewById(R.id.loading_music).setVisibility(View.GONE);
                        act.findViewById(R.id.play_pause).setVisibility(View.VISIBLE);

                        ImageButton play_pause = (ImageButton) act.findViewById(R.id.play_pause);
                        play_pause.setImageResource(R.drawable.ic_pause_blue);
                    }
                }
            });
        }
    }
    
    public void pause(){
        app.client_manager.party.playlist.is_playing = false;
        if (act instanceof PlaylistActivity){
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (act instanceof PlaylistActivity) {
                        act.findViewById(R.id.loading_music).setVisibility(View.GONE);
                        act.findViewById(R.id.play_pause).setVisibility(View.VISIBLE);

                        ImageButton play_pause = (ImageButton) act.findViewById(R.id.play_pause);
                        play_pause.setImageResource(R.drawable.ic_play_blue);
                    }
                }
            });
        }
    }

    public void initToolBar(){
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                User user = app.client_manager.user;
                ImageView user_img = (ImageView) act.findViewById(R.id.this_user);

                List<String> img_paths = new ArrayList<String>();
                List<ImageView> views = new ArrayList<ImageView>();

                img_paths.add(user.img_path);
                views.add(user_img);

                imageLoader.loadImage(act,img_paths,views);
            }
        });
    }

    public void initEnterPartyActivity() {

        initToolBar();
        app.gui_manager.curProgress = 0;
        Button create = (Button) act.findViewById(R.id.create);
        final SearchView search_parties = act.findViewById(R.id.search_parties_bar);

        search_parties.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_parties.clearFocus();
                app.client_manager.searchParty(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                EditText searchEditText = (EditText) search_parties.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                searchEditText.setTextColor(Color.WHITE);
                searchEditText.setHintTextColor(Color.WHITE);
                return false;
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchActivity(CreatePartyActivity.class);
            }
        });

        if( adapters.get(0).isEmpty() ){
            act.findViewById(R.id.no_parties).setVisibility(View.VISIBLE);
            act.findViewById(R.id.parties_list).setVisibility(View.GONE);
        }
        else {
            act.findViewById(R.id.no_parties).setVisibility(View.GONE);
            act.findViewById(R.id.parties_list).setVisibility(View.VISIBLE);
        }


        TextView logout = (TextView) act.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager.logout();
                app.mGoogleSignInClient.signOut()
                    .addOnCompleteListener(act, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            switchActivity( MainActivity.class);
                        }
                    });
            }
        });
    }

    public void initLoadingActivity(){
        initToolBar();
        app.client_manager.connectParty(app.client_manager.requested_party);
        app.client_manager.requested_party = null;

        ImageButton back = (ImageButton) act.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (leaveParty) {
                    leaveParty = true;
                }
                app.client_manager.leaveParty();
                switchActivity(EnterPartyActivity.class);
            }
        });
    }

    public void initCreatePartyActivity(){
        initToolBar();

        EditText partyName = (EditText) act.findViewById(R.id.editText);
        partyName.setText(app.client_manager.party.party_name);
        partyName.clearFocus();

        partyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String party_name = s.toString();
                app.client_manager.party.party_name = party_name;
            }
        });

        final ImageView isPrivate = (ImageView) act.findViewById(R.id.isPrivate);
        if (!app.client_manager.party.is_private)
            isPrivate.setImageResource(R.drawable.ic_unlock_blue);

        isPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.client_manager.party.is_private) {
                    isPrivate.setImageResource(R.drawable.ic_lock_blue);
                    app.client_manager.party.is_private = true;
                }
                else {
                    isPrivate.setImageResource(R.drawable.ic_unlock_blue);
                    app.client_manager.party.is_private = false;
                }
            }
        });

        ImageButton back = (ImageButton) act.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivity(EnterPartyActivity.class);
            }
        });

        searchAndUpdate("");

    }

    public void initConnectedActivity() {
        initToolBar();

        final TextView req_title = (TextView) act.findViewById(R.id.connected2);

        if (!app.client_manager.party.is_private){
            req_title.setVisibility(View.GONE);
            ((ConnectedActivity)act).request_list.setVisibility(View.GONE);
        }
        else{
            req_title.setVisibility(View.VISIBLE);
            ((ConnectedActivity)act).request_list.setVisibility(View.VISIBLE);
        }

        final EditText partyName = (EditText) act.findViewById(R.id.editText);
        partyName.setText(app.client_manager.party.party_name);
        partyName.clearFocus();

        partyName.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        partyName.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                actionId == EditorInfo.IME_ACTION_SEND ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                partyName.clearFocus();
                                InputMethodManager imm = (InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                                String party_name = partyName.getText().toString();
                                app.client_manager.changePartyName(party_name);
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );

        final ImageView isPrivate = (ImageView) act.findViewById(R.id.isPrivate);
        if (!app.client_manager.party.is_private)
            isPrivate.setImageResource(R.drawable.ic_unlock_blue);
        else
            isPrivate.setImageResource(R.drawable.ic_lock_blue);

        isPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.client_manager.party.is_private) {
                    isPrivate.setImageResource(R.drawable.ic_lock_blue);
                    app.client_manager.turnToPrivate();
                    req_title.setVisibility(View.VISIBLE);
                    ((ConnectedActivity)act).request_list.setVisibility(View.VISIBLE);

                    ((BaseAdapter)adapters.get(0)).notifyDataSetChanged();
                    ((BaseAdapter)adapters.get(1)).notifyDataSetChanged();
                }
                else {
                    isPrivate.setImageResource(R.drawable.ic_unlock_blue);
                    app.client_manager.turnToPublic();
                    req_title.setVisibility(View.GONE);
                    ((ConnectedActivity)act).request_list.setVisibility(View.GONE);

                    ((BaseAdapter)adapters.get(0)).notifyDataSetChanged();
                    ((BaseAdapter)adapters.get(1)).notifyDataSetChanged();
                }
            }
        });

        ImageButton back = (ImageButton) act.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivity(PlaylistActivity.class);
            }
        });
    }

    public void initAddMusicActivity(){
        initToolBar();

        final SearchView search_bar = (SearchView) act.findViewById(R.id.search_bar);

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_bar.clearFocus();
                searchAndUpdate(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                EditText searchEditText = (EditText) search_bar.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                searchEditText.setTextColor(Color.WHITE);
                searchEditText.setHintTextColor(Color.WHITE);
                return false;
            }
        });

        ImageButton back = (ImageButton) act.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivity(PlaylistActivity.class);
            }
        });

        searchAndUpdate("");
    }

    public void initPlaylistActivity(){
        initToolBar();
        isPlaylistOnTop = true;

        if(mediaPlayer == null && app.media_manager.active_mp != -1){
            mediaPlayer = app.media_manager.players[app.media_manager.active_mp];
        }

        ProgressBar progressBar = (ProgressBar) act.findViewById(R.id.progressBar_music);
        //progressBar.setProgress(curProgress);
        progressBar.getProgressDrawable().setColorFilter(
                Color.parseColor("#00faf1"), android.graphics.PorterDuff.Mode.SRC_IN);


        SeekBar seekBar = (SeekBar) act.findViewById(R.id.seekBar_music);
        //seekBar.setProgress(curProgress);
        seekBar.getProgressDrawable().setColorFilter(
                Color.parseColor("#00faf1"), android.graphics.PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(curProgress);
                synchronized (validOffset) {
                    validOffset = false;
                }
                if(mediaPlayer != null) {
                    try{
                        if(seekBar.getMax() <= 100 || seekBar.getMax() != mediaPlayer.getDuration()) {
                            //curProgress = (curProgress * mediaPlayer.getDuration()) / 100;
                            seekBar.setMax(100);
                            seekBar.setProgress(0);
                            curProgress = 0;
                            return;
                        }
                        app.media_manager.pause();
                        int id = app.client_manager.party.playlist.tracks.get(app.client_manager.party.playlist.cur_track).track_id;
                        app.client_manager.seekMusic(id, curProgress);
                    }
                    catch (Exception e){
                        seekBar.setProgress(0);
                        curProgress = 0;
                    }
                }
                else{
                    seekBar.setProgress(0);
                    curProgress = 0;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    seekBar.setProgress(progress);
                    synchronized (curProgress) {
                        curProgress = progress;
                    }
                }
            }
        });

        if(app.client_manager.isAdmin()){
            seekBar.setVisibility(View.VISIBLE);
            seekBar.setProgress(curProgress);
            progressBar.setVisibility(View.GONE);
        }
        else{
            seekBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(curProgress);
        }

        if (mediaPlayer != null){
            if(app.client_manager.isAdmin())
                startSeekBar(mediaPlayer,curProgress);
            else
                startProgressBar(mediaPlayer,curProgress);
        }
        final ImageButton mute = (ImageButton) act.findViewById(R.id.mute);
        final ImageButton play_pause = (ImageButton) act.findViewById(R.id.play_pause);
        ImageButton next = (ImageButton) act.findViewById(R.id.next);
        ImageButton connected = (ImageButton) act.findViewById(R.id.connecting);
        ImageButton add = (ImageButton) act.findViewById(R.id.add);
        TextView party_name = (TextView) act.findViewById(R.id.party_name);
        TextView party_name_conn = (TextView) act.findViewById(R.id.party_name_conn);
        final ImageButton mute_conn = (ImageButton) act.findViewById(R.id.mute_conn);
        final ImageButton sync_music = (ImageButton) act.findViewById(R.id.sync_music);

        party_name.setText(app.client_manager.party.party_name);
        party_name_conn.setText(app.client_manager.party.party_name);

        if(app.client_manager.waiting_for_response){
            act.findViewById(R.id.loading_music).setVisibility(View.VISIBLE);
            act.findViewById(R.id.play_pause).setVisibility(View.GONE);
        }
        else{
            act.findViewById(R.id.loading_music).setVisibility(View.GONE);
            act.findViewById(R.id.play_pause).setVisibility(View.VISIBLE);
        }

        if(app.client_manager.party.playlist.is_playing)
            play_pause.setImageResource(R.drawable.ic_pause_blue);
        else
            play_pause.setImageResource(R.drawable.ic_play_blue);


        if(app.media_manager.isMute()) {
            mute.setImageResource(R.drawable.ic_mute_blue);
            mute_conn.setImageResource(R.drawable.ic_mute_blue);
        }
        else {
            mute.setImageResource(R.drawable.ic_unmute_blue);
            mute_conn.setImageResource(R.drawable.ic_unmute_blue);
        }


        connected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivity(ConnectedActivity.class);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivity(AddMusicActivity.class);
            }
        });

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager.party.playlist.is_playing = !app.client_manager.party.playlist.is_playing;
                app.client_manager.commandPlayPause();
                if(app.client_manager.party.playlist.is_playing){
                     act.runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             ProgressBar b = act.findViewById(R.id.loading_music);
                             b.getIndeterminateDrawable()
                                     .setColorFilter(ContextCompat.getColor(act, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
                             act.findViewById(R.id.loading_music).setVisibility(View.VISIBLE);
                             act.findViewById(R.id.play_pause).setVisibility(View.GONE);
                         }
                     });
                }
                else
                    play_pause.setImageResource(R.drawable.ic_play_blue);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOffset(0);
                if(app.client_manager.party.playlist.is_playing) {
                    ProgressBar b = act.findViewById(R.id.loading_music);
                    b.getIndeterminateDrawable()
                            .setColorFilter(ContextCompat.getColor(act, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    act.findViewById(R.id.loading_music).setVisibility(View.VISIBLE);
                    act.findViewById(R.id.play_pause).setVisibility(View.GONE);
                }
                app.client_manager.nextSong(userIntention.NEXT_BUTTON);
            }
        });

        mute.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(app.media_manager.isMute()) {
                    app.media_manager.unmute();
                    mute.setImageResource(R.drawable.ic_unmute_blue);
                }
                else {
                    app.media_manager.mute();
                    mute.setImageResource(R.drawable.ic_mute_blue);
                }
            }
        });

        mute_conn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(app.media_manager.isMute()) {
                    app.media_manager.unmute();
                    mute_conn.setImageResource(R.drawable.ic_unmute_blue);
                }
                else {
                    app.media_manager.mute();
                    mute_conn.setImageResource(R.drawable.ic_mute_blue);
                }
            }
        });

        if (app.client_manager.isAdmin()){
            act.findViewById(R.id.admin_toolbar).setVisibility(View.VISIBLE);
            act.findViewById(R.id.connected_toolbar).setVisibility(View.GONE);
        }
        else {
            act.findViewById(R.id.admin_toolbar).setVisibility(View.GONE);
            act.findViewById(R.id.connected_toolbar).setVisibility(View.VISIBLE);
        }

        sync_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(app.client_manager.party.playlist.is_playing && act instanceof PlaylistActivity){
                    app.media_manager.pause();
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            act.findViewById(R.id.loading_music).setVisibility(View.VISIBLE);
                            act.findViewById(R.id.play_pause).setVisibility(View.GONE);

                        }
                    });
                    app.client_manager.syncMusic();
                }

            }
        });

        TextView leave_party = (TextView) act.findViewById(R.id.leave);
        leave_party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager.leaveParty();
                if(!(act instanceof EnterPartyActivity)) {
                    switchActivity(EnterPartyActivity.class);
                }
            }
        });
    }

    public void syncParty(final int old_cur_track, final boolean isPlaying) {
        if (act instanceof ConnectedActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (cur_changes) {
                        Iterator<GUIChange> iter = cur_changes.iterator();
                        while (iter.hasNext()) {
                            GUIChange c = iter.next();
                            switch (c) {
                                case users:
                                    ((BaseAdapter) adapters.get(0)).notifyDataSetChanged();
                                    act.findViewById(R.id.connected_list).refreshDrawableState();
                                    break;
                                case requests:
                                    ((BaseAdapter) adapters.get(1)).notifyDataSetChanged();
                                    act.findViewById(R.id.waiting_list).refreshDrawableState();
                                    break;
                                case is_private:
                                    TextView req_title = (TextView) act.findViewById(R.id.connected2);
                                    ImageView isPrivate = (ImageView) act.findViewById(R.id.isPrivate);
                                    if (!app.client_manager.party.is_private) {
                                        isPrivate.setImageResource(R.drawable.ic_unlock_blue);
                                        req_title.setVisibility(View.GONE);
                                        ((ConnectedActivity) act).request_list.setVisibility(View.GONE);
                                    } else {
                                        isPrivate.setImageResource(R.drawable.ic_lock_blue);
                                        req_title.setVisibility(View.VISIBLE);
                                        ((ConnectedActivity) act).request_list.setVisibility(View.VISIBLE);
                                    }
                                    act.findViewById(R.id.change_name).refreshDrawableState();
                                    break;
                                case party_name:
                                    EditText partyName = (EditText) act.findViewById(R.id.editText);
                                    partyName.setText(app.client_manager.party.party_name);
                                    partyName.clearFocus();
                                    act.findViewById(R.id.change_name).refreshDrawableState();
                                    break;
                            }
                            iter.remove();
                        }
                    }
                }
            });
        }
        if (act instanceof PlaylistActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (cur_changes) {
                        Iterator<GUIChange> iter = cur_changes.iterator();
                        while (iter.hasNext() && act instanceof PlaylistActivity) {
                            GUIChange c = iter.next();
                            switch (c) {
                                case songs:
                                    recycler_adapter.notifyDataSetChanged();
                                    break;
                                case party_name:
                                    TextView party_name = (TextView) act.findViewById(R.id.party_name);
                                    TextView party_name_conn = (TextView) act.findViewById(R.id.party_name_conn);
                                    party_name.setText(app.client_manager.party.party_name);
                                    party_name_conn.setText(app.client_manager.party.party_name);
                                    act.findViewById(R.id.admin_toolbar).refreshDrawableState();
                                    act.findViewById(R.id.connected_toolbar).refreshDrawableState();
                                    break;
                                case admin:
                                    act.findViewById(R.id.admin_toolbar).setVisibility(View.VISIBLE);
                                    act.findViewById(R.id.connected_toolbar).setVisibility(View.GONE);
                                    act.findViewById(R.id.progressBar_music).setVisibility(View.GONE);
                                    act.findViewById(R.id.seekBar_music).setVisibility(View.VISIBLE);
                                    if (mediaPlayer != null)
                                        startSeekBar(mediaPlayer, curProgress);
                                    act.findViewById(R.id.admin_toolbar).refreshDrawableState();
                                    break;
                            }
                        }
                        if (cur_changes.indexOf(GUIChange.cur_track) != -1) {
                            //if(old_cur_track != app.client_manager.party.playlist.cur_track)
                            ((PlaylistRecyclerView) recycler_adapter).setCurTrackBackground(
                                    old_cur_track, app.client_manager.party.playlist.cur_track);
                        }
                        cur_changes.clear();
                    }
                }
            });
        }
        if (act instanceof LoadingActivity && !isPlaying)
            switchActivity(PlaylistActivity.class);
    }

    public void disconnected(Boolean fromListener) {
        try {
            app.semaphoreDisconnected.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (app.client_manager.party != null) {
            if(app.media_manager != null) {
                app.media_manager.players[0].release();
                app.media_manager.players[1].release();
            }
        }
        app.client_manager.terminateConnection(fromListener);

        if (!(act instanceof  NoConnectionActivity)) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(act, NoConnectionActivity.class);
                    act.startActivity(intent);
                }
            });
        }
        app.disconnected = true;
        app.semaphoreDisconnected.release();
    }


    public void searchAndUpdate(final String query){
        new AsyncTask<String, Integer, Playlist>() {
            @Override
            protected Playlist doInBackground(String... strings) {
                if(act instanceof CreatePartyActivity || act instanceof AddMusicActivity) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            act.findViewById(R.id.dancing_balls).setVisibility(View.VISIBLE);
                            act.findViewById(R.id.songlist).setVisibility(View.GONE);
                        }
                    });
                }
                if (query.equals("")) {
                    if (app.gui_manager.searchRes == null) {
                        app.gui_manager.searchRes = app.client_manager.searchTracks(strings[0]);
                    }
                    return app.gui_manager.searchRes;
                }
                return app.client_manager.searchTracks(strings[0]);
            }

            protected void onPostExecute(final Playlist search_res) {
                if(act instanceof CreatePartyActivity || act instanceof AddMusicActivity) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            act.findViewById(R.id.dancing_balls).setVisibility(View.GONE);
                            act.findViewById(R.id.songlist).setVisibility(View.VISIBLE);

                            ((PlaylistList) ((CostumeListAdapter) adapters.get(0)).list_obj).playlist = search_res;
                            ((BaseAdapter) adapters.get(0)).notifyDataSetChanged();
                        }
                    });
                }
            }
        }.execute(query);
    }

    public void switchActivity(final Class to_act){
        if( act instanceof PlaylistActivity )
            isPlaylistOnTop = false;

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(act, to_act);
                try {
                    app.semaphoreDisconnected.acquire();
                    if (!app.disconnected)
                        act.startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                app.semaphoreDisconnected.release();
            }
        });
    }

    public void playChosen(){
        updateOffset(0);
        if(app.client_manager.party.playlist.is_playing) {
            ProgressBar b = act.findViewById(R.id.loading_music);
            b.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(act, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            act.findViewById(R.id.loading_music).setVisibility(View.VISIBLE);
            act.findViewById(R.id.play_pause).setVisibility(View.GONE);
        }
        app.client_manager.playSongChosen();
    }

    public void startProgressBar(final MyMediaPlayer mediaPlayer, final int curPosition) {
        this.mediaPlayer = mediaPlayer;
        this.curProgress = curPosition;
        if (act instanceof PlaylistActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (act instanceof PlaylistActivity) {
                        final ProgressBar progressBar = (ProgressBar) act.findViewById(R.id.progressBar_music);
                        try {
                            progressBar.setMax(mediaPlayer.getDuration());
                            progressBar.setProgress(curProgress);
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressBar.setMax(100);
                            progressBar.setProgress(0);
                            return;
                        }


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int total = mediaPlayer.getDuration();
                                while (isPlaylistOnTop && mediaPlayer != null && curProgress < total
                                        && mediaPlayer.isPlaying() && !app.client_manager.isAdmin()) {
                                    try {
                                        Thread.sleep(500);
                                        synchronized (curProgress) {
                                            curProgress = mediaPlayer.getCurrentPosition();
                                        }
                                        progressBar.setProgress(curProgress);
                                    } catch (InterruptedException e) {
                                        return;
                                    } catch (Exception e) {
                                        return;
                                    }

                                }
                            }
                        }).start();
                    }
                }
            });
        }

    }

    public void startSeekBar(final MyMediaPlayer mediaPlayer, int curPosition) {
        this.mediaPlayer = mediaPlayer;
        this.curProgress = curPosition;
        if (act instanceof PlaylistActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (act instanceof PlaylistActivity) {
                        final SeekBar seekBar = (SeekBar) act.findViewById(R.id.seekBar_music);

                        try {
                            seekBar.setMax(mediaPlayer.getDuration());
                            seekBar.setProgress(curProgress);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int total = mediaPlayer.getDuration();
                                while (isPlaylistOnTop && mediaPlayer != null && curProgress < total
                                        && mediaPlayer.isPlaying() && app.client_manager.isAdmin()) {
                                    try {
                                        Thread.sleep(500);
                                        synchronized (curProgress) {
                                            if (validOffset && mediaPlayer.isPlaying())
                                                curProgress = mediaPlayer.getCurrentPosition();
                                        }
                                        seekBar.setProgress(curProgress);
                                    } catch (InterruptedException e) {
                                        return;
                                    } catch (Exception e) {
                                        return;
                                    }
                                }
                            }
                        }).start();
                    }
                }
            });
        }
    }

    public void updateOffset(final int offset) {
        synchronized (curProgress) {
            curProgress = offset;
        }
        if (act instanceof PlaylistActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (act instanceof PlaylistActivity) {
                        if (app.client_manager.isAdmin()) {
                            ((SeekBar) act.findViewById(R.id.seekBar_music)).setProgress(offset);
                        } else {
                            ((ProgressBar) act.findViewById(R.id.progressBar_music)).setProgress(offset);
                        }
                    }
                }
            });
        }
    }



}
