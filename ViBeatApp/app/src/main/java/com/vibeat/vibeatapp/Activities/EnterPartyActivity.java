package com.vibeat.vibeatapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vibeat.vibeatapp.ListClasses.CostumeListAdapter;
import com.vibeat.vibeatapp.ListClasses.PartiesList;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.HelperClasses.pictureChange;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class EnterPartyActivity extends AppCompatActivity {

    public MyApplication app;
    ListView listOfParties;

    //private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_party);

        app = (MyApplication) this.getApplication();
        app.listener_thread.current_activity = EnterPartyActivity.this;
        app.client_manager.initLocationTracking(this);

        List<Party> nearby_parties = app.client_manager.getPartiesNearby();

        listOfParties = (ListView) findViewById(R.id.parties_list);
        listOfParties.setAdapter(new CostumeListAdapter(EnterPartyActivity.this,
                                                        new PartiesList(nearby_parties)));

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

        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterPartyActivity.this, CreatePartyActivity.class);
                startActivity(intent);
            }
        });

        ImageButton logout = (ImageButton) findViewById(R.id.back);
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
                Intent intent = new Intent(EnterPartyActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
