package com.vibeat.vibeatapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PeopleCostumAdapter extends BaseAdapter {
    static LayoutInflater inflater = null;

    Context context;
    User[] users;
    int type;

    public PeopleCostumAdapter(Context context, User[] users, int type){
        this.context = context;
        this.users = users;
        this.type = type;
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
        return users[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_of_people, null);
        }

        final int ind = position;

        ImageView img = (ImageView) row.findViewById(R.id.imageUser);
        TextView name = (TextView) row.findViewById(R.id.name);
        TextView admin = (TextView) row.findViewById(R.id.admin);
        ImageView crown = (ImageView) row.findViewById(R.id.adminImage);

        final View row_send = row;
        crown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof connectedActivity)
                    make_admin(row_send, context, ind);
            }
        });

        img.setImageResource(users[position].icon_id);
        //img.setImageResource(R.drawable.add);
        name.setText(users[position].getName());
        admin.setTextColor(Color.TRANSPARENT);
        crown.setImageResource(R.drawable.ok);

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), users[position].icon_id);
        bm = pictureChange.getCroppedBitmap(bm);
        img.setImageBitmap(bm);

        if (this.type == 0) {
            crown.setImageResource(R.drawable.chess_not);
            if (users[position].is_admin) {
                crown.setImageResource(R.drawable.chess);
                admin.setTextColor(Color.parseColor("#ff9c40"));
            }
        }
        return row;
    }

    private void make_admin(View v, Context c, int position) {

        this.users[position].is_admin = true;
        ImageView img = v.findViewById(R.id.adminImage);
        TextView admin = v.findViewById(R.id.admin);
        img.setImageResource(R.drawable.chess);
        admin.setTextColor(Color.parseColor("#ff9c40"));
        v.invalidate();
    }
}
