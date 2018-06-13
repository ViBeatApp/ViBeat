package com.vibeat.vibeatapp.Managers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vibeat.vibeatapp.Activities.AddMusicActivity;
import com.vibeat.vibeatapp.Activities.ConnectedActivity;
import com.vibeat.vibeatapp.Activities.CreatePartyActivity;
import com.vibeat.vibeatapp.Activities.EnterPartyActivity;
import com.vibeat.vibeatapp.Activities.LoadingActivity;
import com.vibeat.vibeatapp.Activities.MainActivity;
import com.vibeat.vibeatapp.Activities.PlaylistActivity;
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
import com.vibeat.vibeatapp.imageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GUIManager{
    public Activity act;
    List<Adapter> adapters;
    MyApplication app;
    RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder> recycler_adapter;
    public List<change> cur_changes;


    public GUIManager(Activity act, List<Adapter> adapters){
        this.act = act;
        this.adapters = adapters;
        this.recycler_adapter = null;
        app = (MyApplication) act.getApplication();
        this.cur_changes = new ArrayList<change>();
        //createTimer();
    }

    public GUIManager(Activity act, RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder> adapter){
        this.act = act;
        this.recycler_adapter = adapter;
        this.adapters = null;
        app = (MyApplication) act.getApplication();
        //createTimer();
    }

    /*public void createTimer(){
        Timer timer = new Timer();
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                if(adapters != null) {
                    for (Adapter a : adapters) {
                        ((BaseAdapter) a).notifyDataSetChanged();
                    }
                }
                if(recycler_adapter != null) {
                    recycler_adapter.notifyDataSetChanged();
                }
            }
        };

        timer.schedule(myTask, 1000, 1000);
    }*/


    public void changeActivity(Activity act, List<Adapter> adapters){
        this.act = act;
        this.adapters = adapters;
        this.recycler_adapter = null;
        app = (MyApplication) act.getApplication();
    }

    public void changeActivity(Activity act, RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder> adapter){
        this.act = act;
        this.adapters = null;
        this.recycler_adapter = adapter;
        app = (MyApplication) act.getApplication();
    }

    public void login() {
        app.client_manager.initLocationTracking(act);
        Intent intent = new Intent(act, EnterPartyActivity.class);
        act.startActivity(intent);
    }

    public void requestJoin(partyInfo party) {
        app.client_manager.requested_party = party;
        Intent intent = new Intent(act, LoadingActivity.class);
        act.startActivity(intent);
    }

    public void completeJoin(){
        Intent intent = new Intent(act, PlaylistActivity.class);
        act.startActivity(intent);
    }

    public void putPartyResults(List<partyInfo> party_list) {
        if(act instanceof EnterPartyActivity) {
            final CostumeListAdapter adap =(CostumeListAdapter) adapters.get(0);
            ((PartiesList) adap.list_obj).nearby_parties = party_list;
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adap.notifyDataSetChanged();
                    act.findViewById(R.id.parties_list).refreshDrawableState();
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
                    if (!(act instanceof EnterPartyActivity)) {
                        Intent intent = new Intent(act, EnterPartyActivity.class);
                        act.startActivity(intent);
                    }
                }
            });
        }
    }

    public void songChosen(Track track) {
        if (act instanceof CreatePartyActivity) {
            app.client_manager.createParty();
        }
        /*int prev_size = app.client_manager.party.playlist.tracks.size();
        Log.d("MediaManager", "song_chosen prev playlist size = "+prev_size);*/
        app.client_manager.addTrack(track);
        /*if(prev_size == 1){
            Log.d("MediaManager", "prepare 2nd song");
            app.media_manager.prepare2nd(track.track_id);
        }*/
        Intent intent = new Intent(act, PlaylistActivity.class);
        act.startActivity(intent);
    }

    public void play(int play_track_id) {

        app.client_manager.party.playlist.cur_track = app.client_manager.party.playlist.searchTrack(play_track_id);
        app.client_manager.party.playlist.is_playing = true;
        if (act instanceof PlaylistActivity){
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    act.findViewById(R.id.loading_music).setVisibility(View.GONE);
                    act.findViewById(R.id.play_pause).setVisibility(View.VISIBLE);

                    ImageButton play_pause = (ImageButton) act.findViewById(R.id.play_pause);
                    play_pause.setImageResource(R.drawable.ic_pause_blue);
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
                    ImageButton play_pause = (ImageButton) act.findViewById(R.id.play_pause);
                    play_pause.setImageResource(R.drawable.ic_play_blue);
                }
            });
        }
    }

    /*public void closeParty() {
        if(app.client_manager.party != null)
            app.client_manager.closeParty();
        if(!(act instanceof EnterPartyActivity)) {
            Intent intent = new Intent(act, EnterPartyActivity.class);
            act.startActivity(intent);
        }
    }*/

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

                imageLoader.loadImage(act,img_paths,views, R.color.stroke);
            }
        });
    }

    public void initEnterPartyActivity() {

        initToolBar();

        Button create = (Button) act.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(act, CreatePartyActivity.class);
                act.startActivity(intent);
            }
        });

        TextView logout = (TextView) act.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager.logout();
                //app.client_manager = null;
                Intent intent = new Intent(act, MainActivity.class);
                act.startActivity(intent);
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
                app.client_manager.leaveParty();
                Intent intent = new Intent(act, EnterPartyActivity.class);
                act.startActivity(intent);
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
                Intent intent = new Intent(act, EnterPartyActivity.class);
                act.startActivity(intent);
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
                Intent intent = new Intent(act, PlaylistActivity.class);
                act.startActivity(intent);
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
                Intent intent = new Intent(act, PlaylistActivity.class);
                act.startActivity(intent);
            }
        });

        searchAndUpdate("");
    }

    public void initPlaylistActivity(){
        initToolBar();

        final ImageButton mute = (ImageButton) act.findViewById(R.id.mute);
        final ImageButton play_pause = (ImageButton) act.findViewById(R.id.play_pause);
        ImageButton next = (ImageButton) act.findViewById(R.id.next);
        ImageButton connected = (ImageButton) act.findViewById(R.id.connecting);
        ImageButton add = (ImageButton) act.findViewById(R.id.add);
        TextView party_name = (TextView) act.findViewById(R.id.party_name);
        TextView party_name_conn = (TextView) act.findViewById(R.id.party_name_conn);
        final ImageButton mute_conn = (ImageButton) act.findViewById(R.id.mute_conn);

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
                Intent intent = new Intent(act, ConnectedActivity.class);
                act.startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(act, AddMusicActivity.class);
                act.startActivity(intent);
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
                             //b.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
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
                if(app.client_manager.party.playlist.is_playing) {
                    ProgressBar b = act.findViewById(R.id.loading_music);
                    //b.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    b.getIndeterminateDrawable()
                            .setColorFilter(ContextCompat.getColor(act, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    act.findViewById(R.id.loading_music).setVisibility(View.VISIBLE);
                    act.findViewById(R.id.play_pause).setVisibility(View.GONE);
                    //recycler_adapter.notifyDataSetChanged();
                }
                app.client_manager.nextSong();
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

        TextView leave_party = (TextView) act.findViewById(R.id.leave);
        leave_party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager.leaveParty();
                if(!(act instanceof EnterPartyActivity)) {
                    Intent intent = new Intent(act, EnterPartyActivity.class);
                    act.startActivity(intent);
                }
            }
        });
    }

    public void syncParty(final int old_cur_track) {
        if (act instanceof ConnectedActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Iterator<change> iter = cur_changes.iterator();
                    while (iter.hasNext()) {
                        change c = iter.next();
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

                                ImageView isPrivate = (ImageView) act.findViewById(R.id.isPrivate);
                                if (!app.client_manager.party.is_private)
                                    isPrivate.setImageResource(R.drawable.ic_unlock_blue);
                                else
                                    isPrivate.setImageResource(R.drawable.ic_lock_blue);
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
            });
        }
        if (act instanceof PlaylistActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Iterator<change> iter = cur_changes.iterator();
                    while (iter.hasNext()) {
                        change c = iter.next();
                        switch (c) {
                            case songs:
                                recycler_adapter.notifyDataSetChanged();
                                act.findViewById(R.id.playlist).refreshDrawableState();
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
                                act.findViewById(R.id.admin_toolbar).refreshDrawableState();
                                break;
                            case cur_track:
                                if(cur_changes.indexOf(change.songs) == -1){
                                    // assume that the previous current track is cur_trak - 1.
                                    ((PlaylistRecyclerView)recycler_adapter).setCurTrackBackground(
                                            old_cur_track,app.client_manager.party.playlist.cur_track);
                                }
                                break;
                        }
                    }
                    cur_changes.clear();
                }
            });
        }
        if (act instanceof LoadingActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(act, PlaylistActivity.class);
                    act.startActivity(intent);
                }
            });
        }
    }

    public void disconnected(Boolean fromListener) {
        if (app.client_manager.party != null) {
            if (app.client_manager.party.playlist.is_playing)
                app.media_manager.stop();
        }
        app.client_manager.terminateConnection(fromListener);
        if(!(act instanceof MainActivity)) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(act, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(act);
                    }
                    builder.setTitle("Connection Error")
                            .setMessage("Please try to reopen the app :)")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent homeIntene = new Intent(Intent.ACTION_MAIN);
                                    homeIntene.addCategory(Intent.CATEGORY_HOME);
                                    homeIntene.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    act.startActivity(homeIntene);
                                    //android.os.Process.killProcess(android.os.Process.myPid());
                                    //System.exit(0);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }
    }


    public void searchAndUpdate(String query){
        AsyncTask<String, Integer, Playlist> searchForSongs_thread = new AsyncTask<String, Integer, Playlist>() {
            @Override
            protected Playlist doInBackground(String... strings) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.findViewById(R.id.dancing_balls).setVisibility(View.VISIBLE);
                        act.findViewById(R.id.songlist).setVisibility(View.GONE);
                    }
                });
                Playlist search_res = app.client_manager.searchTracks(strings[0]);
                for (Track track : search_res.tracks) {
                    Glide.with(act)
                            .load(track.img_path)
                            .downloadOnly(400,400);
                }
                return search_res;
            }

            protected void onPostExecute(final Playlist search_res) {
                Log.d("DB","post execute");
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
        }.execute(query);
    }
}
