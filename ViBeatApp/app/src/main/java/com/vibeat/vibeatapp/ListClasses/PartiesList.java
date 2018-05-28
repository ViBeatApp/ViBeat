package com.vibeat.vibeatapp.ListClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vibeat.vibeatapp.HelperClasses.pictureChange;
import com.vibeat.vibeatapp.ListHelpers.ListAdapterable;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.ServerSide.partyInfo;

import java.util.List;

public class PartiesList implements ListAdapterable {

    public List<partyInfo> nearby_parties;

    public PartiesList(List<partyInfo> nearby_parties){
        this.nearby_parties = nearby_parties;
    }

    @Override
    public View initRow(Adapter adapter, Activity context, View v, final int position) {

        final Activity activity = context;

        if(nearby_parties.size() > 2*position) {
            LinearLayout party1 = (LinearLayout) v.findViewById(R.id.party1);
            ImageView img1 = (ImageView) v.findViewById(R.id.imgParty1);
            TextView name1 = (TextView) v.findViewById(R.id.userParty1);

            String path1 = nearby_parties.get(2 * position).image;
            name1.setText(nearby_parties.get(2 * position).party_name);

            Bitmap bm1 = BitmapFactory.decodeFile(path1);
            bm1 = pictureChange.getCroppedBitmap(bm1);
            bm1 = addWhiteBorder(bm1, 10);
            bm1 = pictureChange.getCroppedBitmap(bm1);
            img1.setImageBitmap(bm1);

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


            Bitmap bm2 = BitmapFactory.decodeFile(path2);
            bm2 = pictureChange.getCroppedBitmap(bm2);
            bm2 = addWhiteBorder(bm2, 10);
            bm2 = pictureChange.getCroppedBitmap(bm2);
            img2.setImageBitmap(bm2);

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
