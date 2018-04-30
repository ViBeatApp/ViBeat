package com.vibeat.vibeatapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class WaitingCostumAdapter extends BaseAdapter {
    static LayoutInflater inflater = null;

    Context context;
    User[] users;
    int type;

    public WaitingCostumAdapter(Context context, User[] users, int type){
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
            row = inflater.inflate(R.layout.list_of_waiting, null);
        }

        final int ind = position;

        ImageView img = (ImageView) row.findViewById(R.id.imageUser);
        TextView name = (TextView) row.findViewById(R.id.name);
        ImageView no = (ImageView) row.findViewById(R.id.no);
        ImageView ok = (ImageView) row.findViewById(R.id.ok);

        final View row_send = row;
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof connectedActivity)
                    add_user(row_send, context, ind);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof connectedActivity)
                    remove_user(row_send, context, ind);
            }
        });

        img.setImageResource(R.drawable.add); //img.setImageResource(users[position].icon_id);
        name.setText(users[position].getName());
        ok.setImageResource(R.drawable.ok);
        no.setImageResource(R.drawable.no);

        //Bitmap bm = BitmapFactory.decodeResource(context.getResources(), users[position].icon_id);
        //bm = pictureChange.getCroppedBitmap(bm);
        //img.setImageBitmap(bm);

        return row;
    }

    private void add_user(View v, Context c, int position) {

        this.users[position].is_admin = true;
        ImageView img = v.findViewById(R.id.adminImage);
        TextView admin = v.findViewById(R.id.admin);
        img.setImageResource(R.drawable.chess);
        admin.setTextColor(Color.parseColor("#FF0099CC"));
        v.invalidate();
    }

    private void remove_user(View v, Context c, int position) {

        this.users[position].is_admin = true;
        ImageView img = v.findViewById(R.id.adminImage);
        TextView admin = v.findViewById(R.id.admin);
        img.setImageResource(R.drawable.chess);
        admin.setTextColor(Color.parseColor("#FF0099CC"));
        v.invalidate();
    }
}
