package com.vibeat.vibeatapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.R;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {

    public MyApplication app;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        app = (MyApplication) this.getApplication();
        app.listener_thread.current_activity = LoadingActivity.this;

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

        app.client_manager.connectParty();

        /*timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent;
                app.client_manager.connectParty();
            }
        }, 4*1000);*/

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadingActivity.this, EnterPartyActivity.class);
                startActivity(intent);
            }
        });
    }
}
