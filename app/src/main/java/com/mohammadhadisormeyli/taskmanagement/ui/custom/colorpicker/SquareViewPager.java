package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker;

import android.content.Context;
import android.util.AttributeSet;

import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.view.ViewPager;


public final class SquareViewPager extends ViewPager {
    public SquareViewPager(Context context) {
        super(context);
    }


    public SquareViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() <= 1) {
            return;
        }

        int titleStripHeight = getChildAt(0).getMeasuredHeight();

        int width = getMeasuredWidth();

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(titleStripHeight + width, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
