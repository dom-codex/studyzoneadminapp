package com.sparktech.studyzoneadmin.main_menu

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class SwipeController:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX, dY, actionState, isCurrentlyActive)
            .addSwipeLeftBackgroundColor(R.color.uba_red)
            .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
            .create()
            .decorate()

    }
}