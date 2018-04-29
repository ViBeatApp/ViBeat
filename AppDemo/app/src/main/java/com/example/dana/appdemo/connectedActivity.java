package com.example.dana.appdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

public class connectedActivity extends AppCompatActivity {

    ListView listOfPeople;
    ListView pendingList;
    passingInfo info;

    User user0 = new User("Izzy", R.drawable.izzy, 0, true);
    User user1 = new User("Dana Oshri", R.drawable.dana, 1, false);
    User user2 = new User("Idan Cohen", R.drawable.idan, 2, true);
    User user3 = new User("Ido Abulafya", R.drawable.ido, 3, false);
    User user4 = new User("Tomer Solomon", R.drawable.tomer, 4, false);

    User[] users = { user0, user1, user2, user4};
    User[] waiting = {user3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        Intent i = getIntent();
        info = (passingInfo)i.getSerializableExtra("info");

        listOfPeople = (ListView) findViewById(R.id.connected_list);
        listOfPeople.setAdapter(new PeopleCostumeAdapter(connectedActivity.this, users));

        pendingList = (ListView) findViewById(R.id.waiting_list);
        pendingList.setAdapter(new PeopleCostumeAdapter(connectedActivity.this, waiting));

    }
}
