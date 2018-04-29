package com.vibeat.vibeatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class enterPartyActivity extends AppCompatActivity {

    Track song0 = new Track(0, "Haverot Shelach", "Omer Adam", R.drawable.omeradam, false);
    Track song1 = new Track(1, "Toy", "Neta Barzilai", R.drawable.netabrazilai, false);
    Track song2 = new Track(2, "Ratzity", "Eden Ben Zaken", R.drawable.edenbenzaken, false);
    Track song3 = new Track(3, "Up&Up", "Coldplay", R.drawable.coldplay, false);
    Track song4 = new Track(4, "Olay Nedaber", "Nadav Guedj", R.drawable.nadavguedj, false);
    Track songs[] = {song0, song1, song2, song3, song4};

    passingInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_party);

        Intent i = getIntent();
        info = (passingInfo)i.getSerializableExtra("info");

        ImageView user = (ImageView) findViewById(R.id.izzy);
        TextView name = (TextView) findViewById(R.id.hi);

        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.izzy);
        bm = pictureChange.getCroppedBitmap(bm);
        user.setImageBitmap(bm);
        name.setText("Hi, "+info.user_name);

        ImageView user1 = (ImageView) findViewById(R.id.im1);
        TextView name1 = (TextView) findViewById(R.id.n1);

        bm = BitmapFactory.decodeResource(getResources(),R.drawable.dana);
        bm = pictureChange.getCroppedBitmap(bm);
        user1.setImageBitmap(bm);
        name1.setText("Dana Oshri");

        ImageView user2 = (ImageView) findViewById(R.id.im2);
        TextView name2 = (TextView) findViewById(R.id.n2);
        bm = BitmapFactory.decodeResource(getResources(),R.drawable.idan);
        bm = pictureChange.getCroppedBitmap(bm);
        user2.setImageBitmap(bm);
        name2.setText("Idan Cohen");

        ImageView user3 = (ImageView) findViewById(R.id.im3);
        TextView name3 = (TextView) findViewById(R.id.n3);
        bm = BitmapFactory.decodeResource(getResources(),R.drawable.ido);
        bm = pictureChange.getCroppedBitmap(bm);
        user3.setImageBitmap(bm);
        name3.setText("Ido Abulafya");

        ImageView user4 = (ImageView) findViewById(R.id.im4);
        TextView name4 = (TextView) findViewById(R.id.n4);
        bm = BitmapFactory.decodeResource(getResources(),R.drawable.tomer);
        bm = pictureChange.getCroppedBitmap(bm);
        user4.setImageBitmap(bm);
        name4.setText("Tomer Solomon");

        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createParty(v);
            }
        });

        int[] ids = {R.id.group1,R.id.group2,R.id.group3,R.id.group4};

        for(int j = 0; j < 4; j++) {
            LinearLayout group = (LinearLayout) findViewById(ids[j]);
            final int ind = j;
            group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    join(ind);
                }
            });
        }
    }

    public void join(int group) {
        Intent intent = new Intent(this, playlistActivity.class);
        info.group_id = group;
        intent.putExtra("info",info);
        startActivity(intent);
    }

    public void createParty(View v) {
        Intent intent = new Intent(this, createPartyActivity.class);
        info.group_id = 100;
        intent.putExtra("info",info);
        startActivity(intent);
    }
}
