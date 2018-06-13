package com.vibeat.vibeatapp;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vibeat.vibeatapp.HelperClasses.MyTransformation;

import java.util.List;

public class imageLoader {
    public static void loadImage(final Activity act, final List<String> img_paths,final List<ImageView> views){
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(ImageView view : views){
                    view.setImageResource(R.drawable.blank);
                }
            }
        });

        new Thread() {
            public void run() {
                for (int i = 0; i < img_paths.size(); ++i) {
                    try {
                        final int index = i;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                Glide.with(act)
                                    .load(img_paths.get(index))
                                    .transform(new MyTransformation(act))
                                    .error(R.drawable.cardib)
                                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.blank)
                                        .dontAnimate()
                                    .into(views.get(index));
                            }
                        });
                    } catch (Exception e) {
                        Log.d("image", "failed: " + i);
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    public static void loadImageSquare(final Activity act, final List<String> img_paths,final List<ImageView> views){
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(ImageView view : views){
                    view.setImageResource(R.drawable.blank);
                }
            }
        });

        new Thread() {
            public void run() {
                for (int i = 0; i < img_paths.size(); ++i){
                    try {
                        final int index = i;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                Glide.with(act)
                                        .load(img_paths.get(index))
                                        //.centerCrop()
                                        .override(views.get(index).getMeasuredWidth(),views.get(index).getMeasuredHeight())
                                        .error(R.drawable.cardib)
                                        .placeholder(R.drawable.blank)
                                        .dontAnimate()
                                        .into(views.get(index));
                            }
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
