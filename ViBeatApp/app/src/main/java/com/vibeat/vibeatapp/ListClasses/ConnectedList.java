package com.vibeat.vibeatapp.ListClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeat.vibeatapp.ListHelpers.ListAdapterable;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.HelperClasses.imageLoader;

import java.util.ArrayList;
import java.util.List;

public class ConnectedList implements ListAdapterable {
    Party party;
    MyApplication app;

    public ConnectedList(Party party, MyApplication app){
        this.party = party;
        this.app = app;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View initRow(final Adapter adapter, Activity activity, View v, int position) {
        final int ind = position;

        ImageView img = (ImageView) v.findViewById(R.id.imageUser);
        TextView name = (TextView) v.findViewById(R.id.name);
        final ImageView crown = (ImageView) v.findViewById(R.id.adminImage);

        final View row_send = v;
        final Activity context = activity;

        List<String> img_paths = new ArrayList<String>();
        List<ImageView> views = new ArrayList<ImageView>();

        if (position < party.admin.size()) {
            img_paths.add(party.admin.get(position).img_path);
            name.setText(party.admin.get(position).name);
            crown.setImageResource(R.drawable.white_crown_full);
        } else {
            img_paths.add(party.connected.get(position - party.admin.size()).img_path);
            name.setText(party.connected.get(position - party.admin.size()).name);
            crown.setImageResource(R.drawable.white_crown);
        }
        views.add(img);
        imageLoader.loadImage(activity, img_paths, views);

        crown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ind >= party.admin.size()) {
                    app.client_manager.makeAdmin(party.connected.get(ind - party.admin.size()));
                    crown.setImageResource(R.drawable.white_crown_full);
                }
            }
        });

        return v;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_of_people;
    }

    @Override
    public int getCount() {
        return party.connected.size() + party.admin.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < party.admin.size())
            return party.admin.get(position);
        else
            return party.connected.get(position - party.admin.size());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
