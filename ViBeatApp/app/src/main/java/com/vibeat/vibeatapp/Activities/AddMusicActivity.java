package com.vibeat.vibeatapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.ListView;
import android.widget.TextView;

import com.vibeat.vibeatapp.ListClasses.CostumeListAdapter;
import com.vibeat.vibeatapp.ListClasses.PlaylistList;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Playlist;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.R;

public class AddMusicActivity extends AppCompatActivity {
    /*
    Track song0 = new Track(0, "Haverot Shelach", "Omer Adam", R.drawable.omeradam, false);
    Track song1 = new Track(1, "Toy", "Neta Barzilai", R.drawable.netabrazilai, false);
    Track song2 = new Track(2, "Ratzity", "Eden Ben Zaken", R.drawable.edenbenzaken, false);
    Track song3 = new Track(3, "Up&Up", "Coldplay", R.drawable.coldplay, false);
    Track song4 = new Track(4, "Olay Nedaber", "Nadav Guedj", R.drawable.nadavguedj, false);
    Track[] songs = {song0, song1, song2, song3, song4};
    Track[] no_songs = {};*/

    ListView listOfSongs;
    MyApplication app;
    Playlist search_res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);

        app = (MyApplication) this.getApplication();
        final SearchView search_bar = (SearchView) findViewById(R.id.search);

        final PlaylistList disp_res = new PlaylistList(app.client_manager.searchTracks(""));
        final CostumeListAdapter adapter = new CostumeListAdapter(AddMusicActivity.this,
                disp_res);

        listOfSongs = (ListView) findViewById(R.id.songlist);
        listOfSongs.setAdapter(adapter);

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


        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_bar.clearFocus();
                disp_res.playlist = app.client_manager.searchTracks(query);
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMusicActivity.this, PlaylistActivity.class);
                startActivity(intent);
            }
        });
    }
}
