package com.vibeat.vibeatapp.ListClasses;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.vibeat.vibeatapp.ListClasses.ListAdapterable;

public class CostumeListAdapter extends BaseAdapter {

    static LayoutInflater inflater = null;
    Context context;
    ListAdapterable list_obj;

    public CostumeListAdapter(Context context, ListAdapterable list_obj){
        this.context = context;
        this.list_obj = list_obj;
    }

    @Override
    public int getCount() {
        return list_obj.getCount();
    }

    @Override
    public Object getItem(int position) {
        return list_obj.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return list_obj.getItemId(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(list_obj.getLayoutId(), null);
        }

        row = list_obj.initRow(this, (Activity)context, row, position);

        return row;
    }

}


