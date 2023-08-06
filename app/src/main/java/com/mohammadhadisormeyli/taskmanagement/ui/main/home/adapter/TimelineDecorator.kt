package com.mohammadhadisormeyli.taskmanagement.ui.main.home.adapter

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils
import java.util.*

class TimelineDecorator : RecyclerView.ItemDecoration() {

    private val indicatorStyle: TimelineView.IndicatorStyle = TimelineView.IndicatorStyle.Filled

    private val indicatorSize: Float = 8.toPx().toFloat()
    private val indicatorYPosition: Float = 0.5f
    private val checkedIndicatorSize: Float? = null
    private val checkedIndicatorStrokeWidth: Float = 2.toPx().toFloat()
    private val lineStyle: TimelineView.LineStyle = TimelineView.LineStyle.Dashed
    private val lineDashLength: Float? = null
    private val lineDashGap: Float? = null
    private val lineWidth: Float? = null
    private val itemType: TimelineView.ItemType = TimelineView.ItemType.HEADER
    private val padding: Float = 8.toPx().toFloat()
    private var position: Position = Position.Left

    @ColorInt
    private val indicatorColor: Int? = null

    @ColorInt
    private val lineColor: Int? = null

    val width = ((indicatorSize * 2) + (padding * 2))

    enum class Position {
        Left,
        Right
    }

    override fun getItemOffsets(
        rect: Rect,
        view: View,
        parent: RecyclerView,
        s: RecyclerView.State
    ) {
        val size = when (indicatorStyle) {
            TimelineView.IndicatorStyle.Filled -> width
            TimelineView.IndicatorStyle.Empty -> width + checkedIndicatorStrokeWidth
            TimelineView.IndicatorStyle.Checked -> width + checkedIndicatorStrokeWidth
        }.toInt()

        when (position) {
            Position.Left ->
                rect.left = size
            Position.Right ->
                rect.right = size
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        c.save()
        c.clipRect(
            parent.paddingLeft,
            parent.paddingTop,
            parent.width,
            parent.height - parent.paddingBottom
        )

        (parent.adapter as? TimelineAdapter).let { adapter ->
            parent.children.forEach {
                val itemPosition = parent.getChildAdapterPosition(it)

                if (itemPosition == RecyclerView.NO_POSITION)
                    return

                val timelineView = TimelineView(context = parent.context)

                timelineView.id = View.generateViewId()

                adapter?.getTimelineViewType(itemPosition)?.let {
                    timelineView.viewType = it
                } ?: timelineView.setType(itemPosition, parent.adapter?.itemCount ?: -1)

                (adapter?.getIndicatorColor(itemPosition) ?: indicatorColor)?.let {
                    timelineView.indicatorColor = it
                }

                (adapter?.getLineColor(itemPosition) ?: lineColor)?.let {
                    timelineView.lineColor = it
                }

                (adapter?.getIndicatorStyle(itemPosition) ?: indicatorStyle).let {
                    timelineView.indicatorStyle = it
                }

                (adapter?.getViewType(itemPosition) ?: itemType).let {
                    timelineView.itemType = it
                }

                (adapter?.getLineStyle(itemPosition) ?: lineStyle).let {
                    timelineView.lineStyle = it
                }

                timelineView.indicatorSize = indicatorSize

                timelineView.indicatorYPosition = indicatorYPosition

                checkedIndicatorSize?.let {
                    timelineView.checkedIndicatorSize = it
                }

                checkedIndicatorStrokeWidth.let {
                    timelineView.checkedIndicatorStrokeWidth = it
                }

                lineDashLength?.let {
                    timelineView.lineDashLength = it
                }

                lineDashGap?.let {
                    timelineView.lineDashGap = it
                }

                lineWidth?.let {
                    timelineView.lineWidth = it
                }

                timelineView.measure(
                    View.MeasureSpec.getSize(width.toInt()),
                    View.MeasureSpec.getSize(it.measuredHeight)
                )

                c.save()
                when (position) {
                    Position.Left -> {
                        c.translate(padding + parent.paddingLeft, it.top.toFloat())
                        timelineView.layout(0, 0, timelineView.measuredWidth, it.measuredHeight)
                    }
                    Position.Right -> {
                        c.translate(
                            it.measuredWidth + padding + parent.paddingLeft,
                            it.top.toFloat()
                        )
                        timelineView.layout(0, 0, timelineView.measuredWidth, it.measuredHeight)
                    }
                }
                timelineView.draw(c)
                c.restore()
            }
        }

        c.restore()
    }

    init {
        if (CommonUtils.isRTL(Locale.getDefault()))
            position = Position.Right
    }
}