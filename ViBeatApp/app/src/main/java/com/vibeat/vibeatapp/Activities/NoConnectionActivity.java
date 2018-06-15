package com.vibeat.vibeatapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.vibeat.vibeatapp.Managers.AuthenticationManager;
import com.vibeat.vibeatapp.Managers.ClientManager;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;

import java.util.ArrayList;

public class NoConnectionActivity extends AppCompatActivity {

    public MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);

        app = (MyApplication) this.getApplication();
        Log.d("Test7", "no connection activity");
        app.disconnected = true;
        app.semaphoreDisconnected.release();

        ListView no_connection = (ListView) findViewById(R.id.no_connection_list);
        no_connection.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;

                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) NoConnectionActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.no_connection, null);
                }

                ViewGroup.LayoutParams params = row.getLayoutParams();
                if (params == null)
                    params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                else
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;

                row.setLayoutParams(params);
                return row;
            }
        });

        app.gui_manager.changeActivity(NoConnectionActivity.this,new ArrayList<Adapter>());

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                new Thread(){
                    @Override
                    public void run() {

                        int counter = 0;
                        if (app.sender_thread != null) {
                            Log.d("Test7", "no connection activity - not null");
                        }
                        while (app.sender_thread == null && counter < 5) {
                            Log.d("Test7", "no connection activity - inside while");
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(NoConnectionActivity.this);
                            app.client_manager = new ClientManager(AuthenticationManager.getGoogleUserFromAccount(account), app);
                            app.semaphore.release();
                            try {
                                app.semaphoreSender.acquire();
                                Log.d("Test7", "no connection activity - after new ClientManager");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            app.gui_manager.login();
                            counter++;

                            try {
                                this.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        });
    }
}
