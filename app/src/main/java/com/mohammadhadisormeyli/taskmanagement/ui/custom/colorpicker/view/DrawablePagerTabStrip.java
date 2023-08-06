package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewConfiguration;

import androidx.viewpager.widget.PagerAdapter;


public class DrawablePagerTabStrip extends DrawablePagerTitleStrip {
    private static final String TAG = "PagerTabStrip";

    private static final int INDICATOR_HEIGHT = 3; // dp
    private static final int MIN_PADDING_BOTTOM = INDICATOR_HEIGHT + 3; // dp
    private static final int TAB_PADDING = 0; // dp
    private static final int TAB_SPACING = 0; // dp
    private static final int MIN_TEXT_SPACING = TAB_SPACING + TAB_PADDING * 2; // dp
    private static final int FULL_UNDERLINE_HEIGHT = 1; // dp
    private static final int MIN_STRIP_HEIGHT = 32; // dp
    private final Paint mTabPaint = new Paint();
    private final Rect mTempRect = new Rect();
    private int mIndicatorColor;
    private int mIndicatorHeight;
    private int mMinPaddingBottom;
    private int mMinTextSpacing;
    private int mMinStripHeight;
    private int mTabPadding;
    private int mTabAlpha = 0xff;

    private boolean mDrawFullUnderline = false;
    private boolean mDrawFullUnderlineSet = false;
    private int mFullUnderlineHeight;

    private boolean mIgnoreTap;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private int mTouchSlop;


    public DrawablePagerTabStrip(Context context) {
        this(context, null);
    }


    public DrawablePagerTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

        mIndicatorColor = 0; // TODO: allow to set color
        mTabPaint.setColor(mIndicatorColor);

        final float density = context.getResources().getDisplayMetrics().density;
        mIndicatorHeight = (int) (INDICATOR_HEIGHT * density + 0.5f);
        mMinPaddingBottom = (int) (MIN_PADDING_BOTTOM * density + 0.5f);
        mMinTextSpacing = (int) (MIN_TEXT_SPACING * density);
        mTabPadding = (int) (TAB_PADDING * density + 0.5f);
        mFullUnderlineHeight = (int) (FULL_UNDERLINE_HEIGHT * density + 0.5f);
        mMinStripHeight = (int) (MIN_STRIP_HEIGHT * density + 0.5f);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        setTextSpacing(getTextSpacing());

        setWillNotDraw(false);

        if (getBackground() == null) {
            mDrawFullUnderline = true;
        }
    }

    public void setTabIndicatorColorResource(int resId) {
        setTabIndicatorColor(getContext().getResources().getColor(resId));
    }

    public int getTabIndicatorColor() {
        return mIndicatorColor;
    }

    public void setTabIndicatorColor(int color) {
        mIndicatorColor = color;
        mTabPaint.setColor(mIndicatorColor);
        invalidate();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (bottom < mMinPaddingBottom) {
            bottom = mMinPaddingBottom;
        }
        super.setPadding(left, top, right, bottom);
    }


    @Override
    public void setTextSpacing(int textSpacing) {
        if (textSpacing < mMinTextSpacing) {
            textSpacing = mMinTextSpacing;
        }
        super.setTextSpacing(textSpacing);
    }


    @Override
    public void setBackgroundDrawable(Drawable d) {
        super.setBackgroundDrawable(d);
        if (!mDrawFullUnderlineSet) {
            mDrawFullUnderline = d == null;
        }
    }


    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        if (!mDrawFullUnderlineSet) {
            mDrawFullUnderline = (color & 0xFF000000) == 0;
        }
    }


    @Override
    public void setBackgroundResource(int resId) {
        super.setBackgroundResource(resId);
        if (!mDrawFullUnderlineSet) {
            mDrawFullUnderline = resId == 0;
        }
    }

    public boolean getDrawFullUnderline() {
        return mDrawFullUnderline;
    }

    public void setDrawFullUnderline(boolean drawFull) {
        mDrawFullUnderline = drawFull;
        mDrawFullUnderlineSet = true;
        invalidate();
    }

    @Override
    int getMinHeight() {
        return Math.max(super.getMinHeight(), mMinStripHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int height = getHeight();
        final int bottom = height;
        final int left = mImageViews[mImageViews.length / 2].getLeft() - mTabPadding;
        final int right = mImageViews[mImageViews.length / 2].getRight() + mTabPadding;
        final int top = bottom - mIndicatorHeight;

        mTabPaint.setColor(mTabAlpha << 24 | (mIndicatorColor & 0xFFFFFF));
        canvas.drawRect(left, top, right, bottom, mTabPaint);
    }


    @Override
    void updateAdapter(PagerAdapter oldAdapter, PagerAdapter newAdapter) {
        super.updateAdapter(oldAdapter, newAdapter);

        if (mImageViews != null) {
            for (int i = 0, l = mImageViews.length; i < l; ++i) {
                if (i == l / 2) {
                    continue;
                }
                final int x = i - l / 2;

                mImageViews[i].setFocusable(true);
                mImageViews[i].setOnClickListener(v -> mPager.setCurrentItem(mPager.getCurrentItem() + x));
            }
        }
    }
}
