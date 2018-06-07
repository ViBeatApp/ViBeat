package com.vibeat.vibeatapp.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.vibeat.vibeatapp.ListClasses.PartiesList;
import com.vibeat.vibeatapp.ListHelpers.CostumeListAdapter;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.ServerSide.partyInfo;

import java.util.ArrayList;
import java.util.List;

public class EnterPartyActivity extends AppCompatActivity {

    public MyApplication app;
    public ListView listOfParties;

    List<partyInfo> nearby_parties = new ArrayList<partyInfo>();

    //private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_party);

        app = (MyApplication) this.getApplication();

        app.client_manager.getPartiesNearby();

        listOfParties = (ListView) findViewById(R.id.parties_list);
        BaseAdapter adap = new CostumeListAdapter(EnterPartyActivity.this,
                new PartiesList(nearby_parties));
        listOfParties.setAdapter(adap);

        List<Adapter> l = new ArrayList<Adapter>();
        l.add(adap);
        app.gui_manager.changeActivity(EnterPartyActivity.this,l);
        app.gui_manager.initEnterPartyActivity();

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                app.client_manager.getPartiesNearby();

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        });


}
}
