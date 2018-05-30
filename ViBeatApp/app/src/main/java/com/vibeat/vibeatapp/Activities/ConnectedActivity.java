package com.vibeat.vibeatapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ListView;

import com.vibeat.vibeatapp.ListClasses.ConnectedList;
import com.vibeat.vibeatapp.ListHelpers.CostumeListAdapter;
import com.vibeat.vibeatapp.ListClasses.RequestList;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;

import java.util.ArrayList;
import java.util.List;

public class ConnectedActivity extends AppCompatActivity {

    public ListView connected_list;
    public ListView request_list;
    MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        app = (MyApplication) this.getApplication();

        final CostumeListAdapter connected_adapter = new CostumeListAdapter(ConnectedActivity.this,
                new ConnectedList(app.client_manager.party,app));
        final CostumeListAdapter request_adapter = new CostumeListAdapter(ConnectedActivity.this,
                new RequestList(app.client_manager.party, connected_adapter));

        connected_list = (ListView) findViewById(R.id.connected_list);
        connected_list.setAdapter(connected_adapter);

        request_list = (ListView) findViewById(R.id.waiting_list);
        request_list.setAdapter(request_adapter);

        List<Adapter> l = new ArrayList<Adapter>();
        l.add(connected_adapter);
        l.add(request_adapter);
        app.gui_manager.changeActivity(ConnectedActivity.this, l);
        app.gui_manager.initConnectedActivity();
    }
}
