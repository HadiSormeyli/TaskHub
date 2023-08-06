package com.mohammadhadisormeyli.taskmanagement.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.mohammadhadisormeyli.taskmanagement.R
import com.mohammadhadisormeyli.taskmanagement.ui.main.home.adapter.toPx

class SemiCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mainPaint: Paint = Paint()
    private var rectangle: RectF? = null
    private var margin: Float
    private var arcProportion: Float = 0f
    private var startAngle: Float = 0f

    init {

        context.theme.obtainStyledAttributes(
            attrs, R.styleable.SemiCircleView, 0, 0
        ).apply {
            mainPaint.isAntiAlias = true
            arcProportion = getFloat(R.styleable.SemiCircleView_arcProportion, 0.8f)
            mainPaint.color = getColor(
                R.styleable.SemiCircleView_paint,
                ContextCompat.getColor(context, R.color.orange_600)
            )
            startAngle = getFloat(R.styleable.SemiCircleView_startAngle, 0f)
            mainPaint.style = Paint.Style.STROKE
            mainPaint.strokeWidth = 5.toPx().toFloat()
            margin = 3.toPx().toFloat()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (rectangle == null) {
            rectangle =
                RectF(0f + margin, 0f + margin, width.toFloat() - margin, height.toFloat() - margin)
        }
        canvas.drawArc(rectangle!!, startAngle + -90f, arcProportion * 360, false, mainPaint)
    }

    fun setArcProportion(arcProportion: Float) {
        this.arcProportion = arcProportion
        invalidate()
    }

}