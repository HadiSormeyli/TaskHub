package com.mohammadhadisormeyli.taskmanagement.ui.main.home.adapter

interface TimelineAdapter {
    fun getTimelineViewType(position: Int): TimelineView.ViewType? = null
    fun getViewType(position: Int): TimelineView.ItemType? = null
    fun getIndicatorStyle(position: Int): TimelineView.IndicatorStyle? = null
    fun getIndicatorColor(position: Int): Int? = null
    fun getLineColor(position: Int): Int? = null
    fun getLineStyle(position: Int): TimelineView.LineStyle? = null
}