package com.vibeat.vibeatapp.ListClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeat.vibeatapp.ListHelpers.ListAdapterable;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.imageLoader;

import java.util.ArrayList;
import java.util.List;

public class ConnectedList implements ListAdapterable {
    Party party;

    public ConnectedList(Party party){
        this.party = party;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View initRow(final Adapter adapter, Activity activity, View v, int position) {
        final int ind = position;

        ImageView img = (ImageView) v.findViewById(R.id.imageUser);
        TextView name = (TextView) v.findViewById(R.id.name);
        TextView admin = (TextView) v.findViewById(R.id.admin);
        ImageView crown = (ImageView) v.findViewById(R.id.adminImage);

        final View row_send = v;
        final Activity context = activity;

        List<String> img_paths = new ArrayList<String>();
        List<ImageView> views = new ArrayList<ImageView>();

        if (position < party.admin.size()) {
            img_paths.add(party.admin.get(position).img_path);
            name.setText(party.admin.get(position).name);
            admin.setTextColor(R.color.text);
            crown.setImageResource(R.drawable.chess);
        } else {
            img_paths.add(party.connected.get(position - party.admin.size()).img_path);
            name.setText(party.connected.get(position - party.admin.size()).name);
            admin.setTextColor(Color.TRANSPARENT);
            crown.setImageResource(R.drawable.chess_not);
        }
        views.add(img);
        imageLoader.loadImage(activity, img_paths, views);

        crown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ind >= party.admin.size()) {
                    party.makeAdmin(party.connected.get(ind - party.admin.size()));
                    ((BaseAdapter) adapter).notifyDataSetChanged();
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
