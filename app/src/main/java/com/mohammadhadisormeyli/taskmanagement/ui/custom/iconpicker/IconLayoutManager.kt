package com.mohammadhadisormeyli.taskmanagement.ui.custom.iconpicker

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler


internal class IconLayoutManager(context: Context, private val iconSize: Int) :
    GridLayoutManager(context, -1, RecyclerView.VERTICAL, false) {

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        if (spanCount == -1 && iconSize > 0 && width > 0 && height > 0) {
            val layoutWidth = width - paddingRight - paddingLeft
            spanCount = layoutWidth / iconSize
        }
        super.onLayoutChildren(recycler, state)
    }
}
