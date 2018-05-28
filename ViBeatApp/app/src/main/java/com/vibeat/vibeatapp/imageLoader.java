package com.vibeat.vibeatapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.vibeat.vibeatapp.HelperClasses.pictureChange;

import java.net.URL;
import java.util.List;

public class imageLoader {
    public static void loadImage(final Activity act, final List<String> img_paths,final List<ImageView> views){
        new Thread() {
            public void run() {
                for (int i = 0; i < img_paths.size(); ++i){
                    try {
                        URL newurl = new URL(img_paths.get(i));
                        final Bitmap bm_before_crop = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());  //Bitmap bm = BitmapFactory.decodeFile(user.img_path);
                        final Bitmap bm = pictureChange.getCroppedBitmap(bm_before_crop);
                        final int index = i;
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Hey jude", "test");
                                views.get(index).setImageBitmap(bm);
                            }
                            //user_img.setImageURI(Uri.parse(user.img_path));
                        });
                    }
                    catch (Exception e) {
                        Log.d("image", "failed: " + i);
                        e.printStackTrace(); }
                }
                }
        }.start();

    }
}
