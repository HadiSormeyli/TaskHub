package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public final class SquareView extends View {
    public SquareView(Context context) {
        super(context);
    }


    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public SquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        setMeasuredDimension(width, width);
    }
}
