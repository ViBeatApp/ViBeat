package com.vibeat.vibeatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PartyCoustumAdapter extends BaseAdapter {

    static LayoutInflater inflater = null;

    Context context;
    User[][] users;
    int type;
    passingInfo info;

    public PartyCoustumAdapter(Context context, User[][] users, passingInfo info){
        this.context = context;
        this.users = users;
        this.info = info;
    }

    @Override
    public int getCount() {
        return users.length;
    }

    @Override
    public Object getItem(int position) {
        return users[position];
    }

    @Override
    public long getItemId(int position) {
        return users[position][0].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_of_parties, null);
        }

        final int ind = position;

        ImageView img1 = (ImageView) row.findViewById(R.id.party1);
        TextView name1 = (TextView) row.findViewById(R.id.userParty1);
        ImageView img2 = (ImageView) row.findViewById(R.id.party2);
        TextView name2 = (TextView) row.findViewById(R.id.userParty2);

        img1.setImageResource(users[position][0].icon_id);
        name1.setText(users[position][0].getName());
        img2.setImageResource(users[position][1].icon_id);
        name2.setText(users[position][1].getName());

        Bitmap bm1 = BitmapFactory.decodeResource(context.getResources(), users[position][0].icon_id);
        bm1 = pictureChange.getCroppedBitmap(bm1);
        img1.setImageBitmap(bm1);

        Bitmap bm2 = BitmapFactory.decodeResource(context.getResources(), users[position][1].icon_id);
        bm2 = pictureChange.getCroppedBitmap(bm2);
        img2.setImageBitmap(bm2);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join(v, context, ind, 0);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join(v, context, ind, 1);
            }
        });

        return row;
    }
    public void join(View v, Context c, int position, int i) {
        Intent intent = new Intent(c, playlistActivity.class);
        info.chosen = position;
        intent.putExtra("info",info);
        c.startActivity(intent);
    }
}
