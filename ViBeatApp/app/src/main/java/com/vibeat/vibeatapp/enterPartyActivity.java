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
import android.widget.ListView;
import android.widget.TextView;

public class enterPartyActivity extends AppCompatActivity {

    ListView listOfParties;

    User user1 = new User("Dana Oshri", R.drawable.dana, 1, false);
    User user2 = new User("Idan Cohen", R.drawable.idan, 2, true);
    User user3 = new User("Ido Abulafya", R.drawable.ido, 3, false);
    User user4 = new User("Tomer Solomon", R.drawable.tomer, 4, false);

    User[][] users = { {user3, user1}, {user2, user4}};
    passingInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_party);

        Intent i = getIntent();
        info = (passingInfo)i.getSerializableExtra("info");

        listOfParties = (ListView) findViewById(R.id.parties_list);
        listOfParties.setAdapter(new PartyCoustumAdapter(enterPartyActivity.this, users, info));

        ImageView user = (ImageView) findViewById(R.id.izzy);
        TextView name = (TextView) findViewById(R.id.hi);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.izzy);
        bm = pictureChange.getCroppedBitmap(bm);
        user.setImageBitmap(bm);

        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createParty(v);
            }
        });
    }

    public void createParty(View v) {
        Intent intent = new Intent(this, createPartyActivity.class);
        info.group_id = 100;
        intent.putExtra("info",info);
        startActivity(intent);
    }
}
