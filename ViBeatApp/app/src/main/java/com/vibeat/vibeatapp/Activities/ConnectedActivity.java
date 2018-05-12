package com.vibeat.vibeatapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.vibeat.vibeatapp.ListClasses.CostumeListAdapter;
import com.vibeat.vibeatapp.ListClasses.ConnectedList;
import com.vibeat.vibeatapp.ListClasses.RequestList;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.R;

public class ConnectedActivity extends AppCompatActivity {

    ListView connected_list;
    ListView request_list;
    MyApplication app;

    /*User user0 = new User("Izzy", R.drawable.izzy, 0, true);
    User user1 = new User("Dana Oshri", R.drawable.dana, 1, false);
    User user2 = new User("Idan Cohen", R.drawable.idan, 2, true);
    User user3 = new User("Ido Abulafya", R.drawable.ido, 3, false);
    User user4 = new User("Tomer Solomon", R.drawable.tomer, 4, false);

    User[] users = { user0, user1, user2, user4};
    User[] waiting = {user3};*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        app = (MyApplication) this.getApplication();
        final CostumeListAdapter connected_adapter = new CostumeListAdapter(ConnectedActivity.this,
                new ConnectedList(app.client_manager.party));
        final CostumeListAdapter request_adapter = new CostumeListAdapter(ConnectedActivity.this,
                new RequestList(app.client_manager.party, connected_adapter));

        connected_list = (ListView) findViewById(R.id.connected_list);
        connected_list.setAdapter(connected_adapter);

        request_list = (ListView) findViewById(R.id.waiting_list);
        request_list.setAdapter(request_adapter);

        final TextView req_title = (TextView) findViewById(R.id.connected2);

        if (!app.client_manager.party.is_private){
            req_title.setVisibility(View.GONE);
            request_list.setVisibility(View.GONE);
        }

        final Party party = app.client_manager.party;

        EditText partyName = (EditText) findViewById(R.id.editText);
        partyName.setText(party.party_name);

        partyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String party_name = s.toString();
                party.party_name = party_name;
            }
        });

        final ImageView isPrivate = (ImageView) findViewById(R.id.isPrivate);
        if (!app.client_manager.party.is_private)
            isPrivate.setImageResource(R.drawable.public_img);

        isPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.client_manager.party.is_private) {
                    isPrivate.setImageResource(R.drawable.private_img);
                    app.client_manager.turnToPrivate();
                    req_title.setVisibility(View.VISIBLE);
                    request_list.setVisibility(View.VISIBLE);

                    connected_adapter.notifyDataSetChanged();
                    request_adapter.notifyDataSetChanged();
                }
                else {
                    isPrivate.setImageResource(R.drawable.public_img2);
                    app.client_manager.turnToPublic();
                    req_title.setVisibility(View.GONE);
                    request_list.setVisibility(View.GONE);

                    connected_adapter.notifyDataSetChanged();
                    request_adapter.notifyDataSetChanged();
                }
            }
        });

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectedActivity.this, PlaylistActivity.class);
                startActivity(intent);
            }
        });
    }
}
