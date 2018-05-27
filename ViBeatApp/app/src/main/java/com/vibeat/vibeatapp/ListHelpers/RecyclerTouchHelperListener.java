package com.vibeat.vibeatapp.ListHelpers;

import android.support.v7.widget.RecyclerView;

public interface RecyclerTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    void onDrag(RecyclerView.ViewHolder viewHolder);
}
