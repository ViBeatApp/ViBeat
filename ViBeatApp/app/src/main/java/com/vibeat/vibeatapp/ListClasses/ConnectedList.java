package com.vibeat.vibeatapp.ListClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeat.vibeatapp.Activities.ConnectedActivity;
import com.vibeat.vibeatapp.HelperClasses.*;
import com.vibeat.vibeatapp.R;

public class ConnectedList implements ListAdapterable {
    passingInfo.Party party;
    int pressed = -1;

    public ConnectedList(passingInfo.Party party){
        this.party = party;
    }

    @Override
    public View initRow(View v, int position) {
        final int ind = position;

        ImageView img = (ImageView) v.findViewById(R.id.imageUser);
        TextView name = (TextView) v.findViewById(R.id.name);
        TextView admin = (TextView) v.findViewById(R.id.admin);
        ImageView crown = (ImageView) v.findViewById(R.id.adminImage);

        final View row_send = v;
        crown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof ConnectedActivity)
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
        return v;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_of_people;
    }

    @Override
    public int getCount() {
        return party.connected.length + party.admin.length;
    }

    @Override
    public Object getItem(int position) {
        if (position < party.admin.length)
            return party.admin[position];
        else
            return party.connected[position - party.admin.length];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
