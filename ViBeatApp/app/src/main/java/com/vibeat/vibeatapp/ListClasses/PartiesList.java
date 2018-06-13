package com.vibeat.vibeatapp.ListClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vibeat.vibeatapp.ListHelpers.ListAdapterable;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.ServerSide.partyInfo;
import com.vibeat.vibeatapp.imageLoader;

import java.util.ArrayList;
import java.util.List;

public class PartiesList implements ListAdapterable {

    public List<partyInfo> nearby_parties;

    public PartiesList(List<partyInfo> nearby_parties){
        this.nearby_parties = nearby_parties;
    }

    @Override
    public View initRow(Adapter adapter, Activity context, View v, final int position) {

        final Activity activity = context;
        List<String> img_paths1 = new ArrayList<String>();
        List<ImageView> views1 = new ArrayList<ImageView>();
        List<String> img_paths2 = new ArrayList<String>();
        List<ImageView> views2 = new ArrayList<ImageView>();

        if(nearby_parties.size() > 2*position) {
            LinearLayout party1 = (LinearLayout) v.findViewById(R.id.party1);
            ImageView img1 = (ImageView) v.findViewById(R.id.imgParty1);
            TextView name1 = (TextView) v.findViewById(R.id.userParty1);

            String path1 = nearby_parties.get(2 * position).image;
            name1.setText(nearby_parties.get(2 * position).party_name);

            img_paths1.add(path1);
            views1.add(img1);
            imageLoader.loadImage(context, img_paths1, views1, R.color.background);

            party1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    partyInfo party = nearby_parties.get(2 * position);
                    ((MyApplication) activity.getApplication()).gui_manager.requestJoin(party);
                }
            });
        }

        if(nearby_parties.size() > 2*position+1) {
            LinearLayout party2 = (LinearLayout) v.findViewById(R.id.party2);
            ImageView img2 = (ImageView) v.findViewById(R.id.imgParty2);
            TextView name2 = (TextView) v.findViewById(R.id.userParty2);

            String path2 = nearby_parties.get(2 * position + 1).image;
            name2.setText(nearby_parties.get(2 * position + 1).party_name);


            img_paths2.add(path2);
            views2.add(img2);
            imageLoader.loadImage(context, img_paths2, views2, R.color.background);

            party2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    partyInfo party = nearby_parties.get(2*position+1);
                    ((MyApplication)activity.getApplication()).gui_manager.requestJoin(party);
                }
            });
        }

        return v;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_of_parties;
    }

    @Override
    public int getCount() {
        return (int) Math.ceil((float) nearby_parties.size()/2);
    }

    @Override
    public Object getItem(int position) {
        return nearby_parties.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(R.color.colorLightStroke);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }
}
