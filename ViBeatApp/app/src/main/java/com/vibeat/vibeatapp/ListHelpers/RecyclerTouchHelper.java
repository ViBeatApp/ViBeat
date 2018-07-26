package com.vibeat.vibeatapp.ListHelpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.vibeat.vibeatapp.ListClasses.PlaylistRecyclerView;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static com.vibeat.vibeatapp.R.color.colorAccentlight;

public class RecyclerTouchHelper extends ItemTouchHelper.Callback{

    public static final float ALPHA_FULL = 1.0f;
    private PlaylistRecyclerView adapter;
    private Context context;
    private MyApplication app;
    public int mFrom = -1;
    private int mTo = -1;

    public RecyclerTouchHelper(PlaylistRecyclerView adapter, Context context, MyApplication app) {

        this.adapter = adapter;
        this.context = context;
        this.app = app;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        if(app.client_manager.isAdmin())
            return true;
        else
            return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        if(app.client_manager.isAdmin())
            return true;
        else
            return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder.getAdapterPosition() < app.client_manager.party.playlist.tracks.size() &&
                viewHolder.getAdapterPosition() >= 0) {
            int track_id = app.client_manager.party.playlist.tracks.get(viewHolder.getAdapterPosition()).track_id;
            if (viewHolder instanceof PlaylistRecyclerView.playlistViewHolder) {
                Log.d("Delet", "second track id = " + track_id);
                if ((!(app.client_manager.party.playlist.tracks.size() == 1)) && track_id != -1 && app.client_manager.isAdmin())
                    adapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        }
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // Set movement flags based on the layout manager
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source,
                          RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType() || !app.client_manager.isAdmin()) {
            return false;
        }
        if(mFrom == -1)
            mFrom = app.client_manager.party.playlist.tracks.get(source.getAdapterPosition()).track_id;
       
        mTo = app.client_manager.party.playlist.tracks.get(target.getAdapterPosition()).track_id;
        source.itemView.setBackgroundColor(colorAccentlight);
        adapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int track_id = -1;
        if(app.client_manager.party.playlist.tracks.size() > viewHolder.getAdapterPosition() &&
                viewHolder.getAdapterPosition() >= 0)
            track_id = app.client_manager.party.playlist.tracks.get(viewHolder.getAdapterPosition()).track_id;
        if(app.client_manager.party.playlist.tracks.size()>1 && track_id != -1 && app.client_manager.isAdmin()) {
           View itemView = viewHolder.itemView;

            if (actionState == ACTION_STATE_SWIPE) {
                int xMarkMargin = 30;
                Drawable deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);

                int itemHeight = itemView.getBottom() - itemView.getTop();

                //Setting Swipe Background
                ColorDrawable background = new ColorDrawable();
                ((ColorDrawable) background).setColor(Color.RED);
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                int intrinsicWidth = deleteIcon.getIntrinsicWidth();
                int intrinsicHeight = deleteIcon.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;


                //Setting Swipe Icon
                deleteIcon.setBounds(xMarkLeft, xMarkTop + 16, xMarkRight, xMarkBottom);
                deleteIcon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState){
        if (actionState == ACTION_STATE_DRAG) {
            viewHolder.itemView.setAlpha(ALPHA_FULL / 2);
            viewHolder.itemView.setBackgroundColor(colorAccentlight);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(ALPHA_FULL);
        viewHolder.itemView.setBackgroundColor(0);

        if(app.client_manager.party.playlist.cur_track == viewHolder.getAdapterPosition())
            viewHolder.itemView.setBackgroundColor(R.color.colorPrimary);
        else
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);

        if (viewHolder instanceof PlaylistRecyclerView.playlistViewHolder) {
            // Tell the view holder it's time to restore the idle state
            if (mFrom != -1 && mTo != -1 && mTo != mFrom) {
                app.client_manager.swapTrack(app.client_manager.party.playlist.searchTrack(mFrom), app.client_manager.party.playlist.searchTrack(mTo));
            }
            mFrom = -1;
            mTo = -1;
            PlaylistRecyclerView.playlistViewHolder itemViewHolder = (PlaylistRecyclerView.playlistViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
    }
}
