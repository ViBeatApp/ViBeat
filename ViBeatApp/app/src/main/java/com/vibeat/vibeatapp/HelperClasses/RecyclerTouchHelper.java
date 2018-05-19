package com.vibeat.vibeatapp.HelperClasses;

import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.vibeat.vibeatapp.ListClasses.PlaylistRecyclerView;

public class RecyclerTouchHelper extends ItemTouchHelper.Callback{

    private PlaylistRecyclerView adapter;

    public RecyclerTouchHelper(PlaylistRecyclerView adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof PlaylistRecyclerView.playlistViewHolder)
            adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // Set movement flags based on the layout manager
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source,
                          RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        adapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
/*
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundView = ((PlaylistRecyclerView.playlistViewHolder)viewHolder).foreground;
        getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);
    }*/

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof PlaylistRecyclerView.playlistViewHolder) {
            // Tell the view holder it's time to restore the idle state
            PlaylistRecyclerView.playlistViewHolder itemViewHolder = (PlaylistRecyclerView.playlistViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
    }

}
