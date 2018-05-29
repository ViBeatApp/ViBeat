package com.vibeat.vibeatapp.ListClasses;
import android.app.Activity;
import android.view.View;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeat.vibeatapp.ListHelpers.ListAdapterable;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.imageLoader;

import java.util.ArrayList;
import java.util.List;

public class RequestList implements ListAdapterable {
    Party party;
    Adapter connected_adapter;

    public RequestList(Party party, Adapter connected_adapter){
        this.party = party;
        this.connected_adapter = connected_adapter;
    }

    @Override
    public View initRow(final Adapter adapter, Activity activity, View v, final int position) {

        final int ind = position;
        final MyApplication app = (MyApplication) activity.getApplication();

        ImageView img = (ImageView) v.findViewById(R.id.imageUser);
        TextView name = (TextView) v.findViewById(R.id.name);
        ImageView no = (ImageView) v.findViewById(R.id.no);
        ImageView ok = (ImageView) v.findViewById(R.id.ok);

        List<String> img_paths = new ArrayList<String>();
        List<ImageView> views = new ArrayList<ImageView>();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager.answerRequest(party.request.get(ind), true);
                ((BaseAdapter)adapter).notifyDataSetChanged();
                ((BaseAdapter)connected_adapter).notifyDataSetChanged();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.client_manager.answerRequest(party.request.get(ind), false);
                ((BaseAdapter)adapter).notifyDataSetChanged();
            }
        });

        ok.setImageResource(R.drawable.ic_ok);
        no.setImageResource(R.drawable.ic_no);

        img_paths.add(party.request.get(position).img_path);
        views.add(img);
        imageLoader.loadImage(activity, img_paths, views);

        name.setText(party.request.get(position).name);

        return v;
    }

    @Override
    public int getLayoutId() {
        return  R.layout.list_of_waiting;
    }

    @Override
    public int getCount() {
        return  party.request.size();
    }

    @Override
    public Object getItem(int position) {
        return party.request.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
