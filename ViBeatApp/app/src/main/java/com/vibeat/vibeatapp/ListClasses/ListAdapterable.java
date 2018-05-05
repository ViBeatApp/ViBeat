package com.vibeat.vibeatapp.ListClasses;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Adapter;

public interface ListAdapterable{

    public View initRow(Adapter adapter, Activity activity, View v, int position);
    public int getLayoutId();

    public int getCount();
    public Object getItem(int position);
    public long getItemId(int position);

}
