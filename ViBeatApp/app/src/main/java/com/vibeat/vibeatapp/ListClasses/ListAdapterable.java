package com.vibeat.vibeatapp.ListClasses;

import android.view.View;

public interface ListAdapterable{

    public View initRow(View v, int position);
    public int getLayoutId();

    public int getCount();
    public Object getItem(int position);
    public long getItemId(int position);

}
