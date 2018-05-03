package com.vibeat.vibeatapp.ListClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeat.vibeatapp.HelperClasses.pictureChange;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.R;

import java.util.List;

public class PartiesList implements ListAdapterable {
    List<Party> nearby_parties;

    public PartiesList(List<Party> nearby_parties){
        this.nearby_parties = nearby_parties;
    }

    @Override
    public View initRow(View v, int position) {

        final int ind = position;

        ImageView img1 = (ImageView) v.findViewById(R.id.party1);
        TextView name1 = (TextView) v.findViewById(R.id.userParty1);
        ImageView img2 = (ImageView) v.findViewById(R.id.party2);
        TextView name2 = (TextView) v.findViewById(R.id.userParty2);

        String path1 = nearby_parties.get(2*position).getCreator().img_path;
        name1.setText(nearby_parties.get(2*position).getCreator().name);
        String path2 = nearby_parties.get(2*position+1).getCreator().img_path;
        name2.setText(nearby_parties.get(2*position+1).getCreator().name);

        Bitmap bm1 = BitmapFactory.decodeFile(path1);
        bm1 = pictureChange.getCroppedBitmap(bm1);
        img1.setImageBitmap(bm1);

        Bitmap bm2 = BitmapFactory.decodeFile(path2);
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

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
