package com.vibeat.vibeatapp.Managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.vibeat.vibeatapp.Objects.Track;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.ServerSide.partyInfo;
import com.vibeat.vibeatapp.imageLoader;

import java.util.ArrayList;
import java.util.List;

public class GUIManager{
    Activity act;
    List<Adapter> adapters;
    MyApplication app;
    RecyclerView.Adapter<PlaylistRecyclerView.playlistViewHolder> recycler_adapter;

    public GUIManager(Activity act, List<Adapter> adapters){
        this.act = act;
        this.adapters = adapters;
        this.recycler_adapter = null;
        app = (MyApplication) act.getApplication();
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
        app.client_manager.connectParty(party);
        Intent intent = new Intent(act, LoadingActivity.class);
        act.startActivity(intent);
    }

    public void completeJoin(){
        Intent intent = new Intent(act, PlaylistActivity.class);
        act.startActivity(intent);
    }

    public void putPartyResults(List<partyInfo> party_list) {
        if(act instanceof EnterPartyActivity) {
            CostumeListAdapter adap =(CostumeListAdapter) adapters.get(0);
            ((PartiesList) adap.list_obj).nearby_parties = party_list;
            adap.notifyDataSetChanged();
        }
    }

    public void rejected() {
        if( act instanceof LoadingActivity ) {
            Toast.makeText(act,
                    "Sorry, your request was not accepted...",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(act, EnterPartyActivity.class);
            act.startActivity(intent);
        }
    }

    public void songChosen(Track track) {
        if (act instanceof CreatePartyActivity) {
            app.client_manager.createParty();
        }
        app.client_manager.addTrack(track);
        Intent intent = new Intent(act, PlaylistActivity.class);
        act.startActivity(intent);
    }

    public void play(int play_track_id) {
        app.client_manager.party.playlist.is_playing = true;
        app.client_manager.party.playlist.cur_track = app.client_manager.party.playlist.searchTrack(play_track_id);
        if (act instanceof PlaylistActivity){
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageButton play_pause = (ImageButton) act.findViewById(R.id.play_pause);
                    play_pause.setImageResource(R.drawable.ic_play);
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
                    play_pause.setImageResource(R.drawable.ic_pause);
                }
            });
        }
    }


    public void initEnterPartyActivity() {
        User user = app.client_manager.user;
        ImageView user_img = (ImageView) act.findViewById(R.id.this_user);
        TextView user_name = (TextView) act.findViewById(R.id.hello_user);
        List<String> img_paths = new ArrayList<String>();
        List<ImageView> views = new ArrayList<ImageView>();
        img_paths .add(user.img_path);
        views.add(user_img);

        imageLoader.loadImage(act,img_paths,views);

        user_name.setText("Hi, "+user.name);

        Button create = (Button) act.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(act, CreatePartyActivity.class);
                act.startActivity(intent);
            }
        });

        ImageButton logout = (ImageButton) act.findViewById(R.id.back);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager.logout();
                app.listener_thread.interrupt();
                try {
                    app.listener_thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                app.client_manager = null;
                Intent intent = new Intent(act, MainActivity.class);
                act.startActivity(intent);
            }
        });
    }

    public void initLoadingActivity(){
        User user = app.client_manager.user;
        ImageView user_img = (ImageView) act.findViewById(R.id.this_user);
        TextView user_name = (TextView) act.findViewById(R.id.hello_user);

        try{
            //URL newurl = new URL(user.img_path);
            //Bitmap bm = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
            //Bitmap bm = BitmapFactory.decodeFile(user.img_path);
            //bm = pictureChange.getCroppedBitmap(bm);
            //user_img.setImageBitmap(bm);.
            user_img.setImageURI(Uri.parse(user.img_path));
        }
        catch (Exception e){}

        user_name.setText("Hi, "+user.name);

        ImageButton back = (ImageButton) act.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(act, EnterPartyActivity.class);
                act.startActivity(intent);
            }
        });
    }

    public void initCreatePartyActivity(){
        User user = app.client_manager.user;
        ImageView user_img = (ImageView) act.findViewById(R.id.this_user);
        TextView user_name = (TextView) act.findViewById(R.id.hello_user);

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
            isPrivate.setImageResource(R.drawable.ic_unlock);

        isPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.client_manager.party.is_private) {
                    isPrivate.setImageResource(R.drawable.ic_lock);
                    app.client_manager.party.is_private = true;
                }
                else {
                    isPrivate.setImageResource(R.drawable.ic_unlock);
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
        });    }

    public void initConnectedActivity() {
        User user = app.client_manager.user;
        ImageView user_img = (ImageView) act.findViewById(R.id.this_user);
        TextView user_name = (TextView) act.findViewById(R.id.hello_user);

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
        final TextView req_title = (TextView) act.findViewById(R.id.connected2);

        if (!app.client_manager.party.is_private){
            req_title.setVisibility(View.GONE);
            ((ConnectedActivity)act).request_list.setVisibility(View.GONE);
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
            isPrivate.setImageResource(R.drawable.ic_unlock);

        isPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.client_manager.party.is_private) {
                    isPrivate.setImageResource(R.drawable.ic_lock);
                    app.client_manager.turnToPrivate();
                    req_title.setVisibility(View.VISIBLE);
                    ((ConnectedActivity)act).request_list.setVisibility(View.VISIBLE);

                    ((BaseAdapter)adapters.get(0)).notifyDataSetChanged();
                    ((BaseAdapter)adapters.get(1)).notifyDataSetChanged();
                }
                else {
                    isPrivate.setImageResource(R.drawable.ic_unlock);
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
        User user = app.client_manager.user;
        ImageView user_img = (ImageView) act.findViewById(R.id.this_user);
        TextView user_name = (TextView) act.findViewById(R.id.hello_user);

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

        final SearchView search_bar = (SearchView) act.findViewById(R.id.search);

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_bar.clearFocus();
                ((PlaylistList)((CostumeListAdapter)adapters.get(0)).list_obj).playlist = app.client_manager.searchTracks(query);
                ((BaseAdapter)adapters.get(0)).notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
    }

    public void initPlaylistActivity(){
        User user = app.client_manager.user;
        ImageView user_img = (ImageView) act.findViewById(R.id.this_user);
        TextView user_name = (TextView) act.findViewById(R.id.hello_user);

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

        final ImageButton mute = (ImageButton) act.findViewById(R.id.mute);

        // admin only
        final ImageButton play_pause = (ImageButton) act.findViewById(R.id.play_pause);
        ImageButton next = (ImageButton) act.findViewById(R.id.next);
        ImageButton connected = (ImageButton) act.findViewById(R.id.connected);
        ImageButton add = (ImageButton) act.findViewById(R.id.add);

        if (app.client_manager.is_admin){
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
                    recycler_adapter.notifyDataSetChanged();
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
                if(app.media_manager.isMute()) {
                    app.media_manager.unmute();
                    mute.setImageResource(R.drawable.ic_mute);
                }
                else {
                    app.media_manager.mute();
                    mute.setImageResource(R.drawable.ic_unmute);
                }
            }
        });

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


    public void syncParty() {
        if (act instanceof ConnectedActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initPlaylistActivity();
                }
            });
        }
        if (act instanceof PlaylistActivity) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initPlaylistActivity();
                }
            });

        }
    }
}
