package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import java.lang.ref.WeakReference;


public class DrawablePagerTitleStrip extends ViewGroup implements ViewPager.Decor {
    private static final String TAG = "PagerTitleStrip";
    private static final int[] ATTRS = new int[]{android.R.attr.gravity};
    private static final int TEXT_SPACING = 16; // dip
    private final PageListener mPageListener = new PageListener();
    ViewPager mPager;
    ImageView[] mImageViews = null;
    private int mLastKnownCurrentPage = -1;
    private float mLastKnownPositionOffset = -1;
    private int mScaledTextSpacing;
    private int mGravity;
    private boolean mUpdatingDrawables;
    private boolean mUpdatingPositions;
    private WeakReference<PagerAdapter> mWatchingAdapter;


    public DrawablePagerTitleStrip(Context context) {
        this(context, null);
    }


    public DrawablePagerTitleStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        mGravity = a.getInteger(0, Gravity.BOTTOM);
        a.recycle();

        final float density = context.getResources().getDisplayMetrics().density;
        mScaledTextSpacing = (int) (TEXT_SPACING * density);
    }

    public int getTextSpacing() {
        return mScaledTextSpacing;
    }

    public void setTextSpacing(int spacingPixels) {
        mScaledTextSpacing = spacingPixels;
        requestLayout();
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
        requestLayout();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final ViewParent parent = getParent();
        if (!(parent instanceof ViewPager)) {
            throw new IllegalStateException("PagerTitleStrip must be a direct child of a ViewPager.");
        }

        final ViewPager pager = (ViewPager) parent;
        final PagerAdapter adapter = pager.getAdapter();

        pager.setInternalPageChangeListener(mPageListener);
        pager.setOnAdapterChangeListener(mPageListener);
        mPager = pager;
        updateAdapter(mWatchingAdapter != null ? mWatchingAdapter.get() : null, adapter);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPager != null) {
            updateAdapter(mPager.getAdapter(), null);
            mPager.setInternalPageChangeListener(null);
            mPager.setOnAdapterChangeListener(null);
            mPager = null;
        }
    }


    void updateImages(int currentItem, PagerAdapter adapter) {
        if (mImageViews == null || mImageViews.length == 0 || !(adapter instanceof IDrawableTitlePagerAdapter)) {
            return;
        }
        IDrawableTitlePagerAdapter dAdapter = (IDrawableTitlePagerAdapter) adapter;

        final int itemCount = adapter.getCount();
        mUpdatingDrawables = true;
        int half = mImageViews.length / 2;
        for (int i = 0, l = mImageViews.length; i < l; ++i) {
            mImageViews[i].setImageDrawable(currentItem + i - half >= 0 && currentItem + i - half < itemCount ? dAdapter
                    .getDrawableTitle(currentItem + i - half) : null);
        }

        final int width = getWidth() - getPaddingLeft() - getPaddingRight();
        final int childHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int childWidthSpec = MeasureSpec.makeMeasureSpec((int) (width * 0.8f), MeasureSpec.AT_MOST);
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);

        for (ImageView iv : mImageViews) {
            iv.measure(childWidthSpec, childHeightSpec);
        }

        mLastKnownCurrentPage = currentItem;

        if (!mUpdatingPositions) {
            updateDrawablePositions(currentItem, mLastKnownPositionOffset, false);
        }

        mUpdatingDrawables = false;
    }


    @Override
    public void requestLayout() {
        if (!mUpdatingDrawables) {
            super.requestLayout();
        }
    }


    void updateAdapter(PagerAdapter oldAdapter, PagerAdapter newAdapter) {
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(mPageListener);
            mWatchingAdapter = null;
        }
        if (newAdapter != null) {
            if (!(newAdapter instanceof IDrawableTitlePagerAdapter)) {
                throw new IllegalArgumentException("Adapter must implement IDrawableTitlePagerAdapter");
            }

            newAdapter.registerDataSetObserver(mPageListener);
            mWatchingAdapter = new WeakReference<PagerAdapter>(newAdapter);

            Context context = getContext();

            // TODO: we should determine the number of images dynamically
            int newCount = Math.max(1, Math.min(9, newAdapter.getCount()));

            if (mImageViews == null || mImageViews.length < newCount) {
                ImageView[] newImages = new ImageView[newCount];
                int start = 0;
                if (mImageViews != null) {
                    System.arraycopy(mImageViews, 0, newImages, 0, mImageViews.length);
                    start = mImageViews.length;
                }

                for (int i = start; i < newCount; ++i) {
                    addView(newImages[i] = new ImageView(context));
                }
                mImageViews = newImages;
            } else if (mImageViews.length > newCount) {
                ImageView[] newImages = new ImageView[newCount];
                System.arraycopy(mImageViews, 0, newImages, 0, newCount);

                for (int i = newCount; i < mImageViews.length; ++i) {
                    removeView(mImageViews[i]);
                }

                mImageViews = newImages;
            }
        } else {
            mImageViews = null;
        }

        if (mPager != null) {
            mLastKnownCurrentPage = -1;
            mLastKnownPositionOffset = -1;
            updateImages(mPager.getCurrentItem(), newAdapter);
            requestLayout();
        }
    }


    @SuppressLint("NewApi")
    void updateDrawablePositions(int position, float positionOffset, boolean force) {
        if (mImageViews == null || mImageViews.length == 0) {
            return;
        }

        if (position != mLastKnownCurrentPage) {
            updateImages(position, mPager.getAdapter());
        } else if (!force && positionOffset == mLastKnownPositionOffset) {
            return;
        }

        mUpdatingPositions = true;

        final int[] widths = new int[mImageViews.length];
        for (int i = 0, l = mImageViews.length; i < l; ++i) {
            widths[i] = mImageViews[i].getMeasuredWidth();
        }

        final int stripWidth = getWidth();
        final int stripHeight = getHeight();
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        float currOffset = positionOffset + 0.5f;
        if (currOffset > 1.f) {
            currOffset -= 1.f;
        }
        final int currCenter = stripWidth / 2 - (int) ((widths[widths.length / 2] + mScaledTextSpacing) * (currOffset - 0.5f));
        final int currLeft = currCenter - widths[widths.length / 2] / 2;

        int maxBaseline = 0;
        for (ImageView iv : mImageViews) {
            maxBaseline = Math.max(maxBaseline, iv.getBaseline());
        }

        int maxTextHeight = 0;
        for (ImageView iv : mImageViews) {
            maxTextHeight = Math.max(maxTextHeight, maxBaseline - iv.getBaseline() + iv.getMeasuredHeight());
        }

        final int vgrav = mGravity & Gravity.VERTICAL_GRAVITY_MASK;

        int left = currLeft;
        for (int i = mImageViews.length / 2, l = mImageViews.length; i < l; ++i) {
            int baseline = mImageViews[i].getBaseline();
            int top;
            switch (vgrav) {
                default:
                case Gravity.TOP:
                    top = paddingTop + maxBaseline - baseline;
                    break;
                case Gravity.CENTER_VERTICAL:
                    final int paddedHeight = stripHeight - paddingTop - paddingBottom;
                    final int centeredTop = (paddedHeight - maxTextHeight) / 2;
                    top = centeredTop + maxBaseline - baseline;
                    break;
                case Gravity.BOTTOM:
                    final int bottomGravTop = stripHeight - paddingBottom - maxTextHeight;
                    top = bottomGravTop + maxBaseline - baseline;
                    break;
            }

            int right = left + widths[i];
            mImageViews[i].layout(left, top, right, top + mImageViews[i].getMeasuredHeight());
            left += widths[i] + mScaledTextSpacing;

            if (stripWidth - right < paddingRight) {
                mImageViews[i].setAlpha(1 - ((float) Math.abs(Math.min(stripWidth - right - paddingRight, 0))) / widths[i]);
            } else {
                mImageViews[i].setAlpha(1f);
            }
        }

        left = currLeft;
        for (int i = mImageViews.length / 2 - 1; i >= 0; --i) {
            int baseline = mImageViews[i].getBaseline();
            int top;
            switch (vgrav) {
                default:
                case Gravity.TOP:
                    top = paddingTop + maxBaseline - baseline;
                    break;
                case Gravity.CENTER_VERTICAL:
                    final int paddedHeight = stripHeight - paddingTop - paddingBottom;
                    final int centeredTop = (paddedHeight - maxTextHeight) / 2;
                    top = centeredTop + maxBaseline - baseline;
                    break;
                case Gravity.BOTTOM:
                    final int bottomGravTop = stripHeight - paddingBottom - maxTextHeight;
                    top = bottomGravTop + maxBaseline - baseline;
                    break;
            }
            left -= widths[i] + mScaledTextSpacing;

            mImageViews[i].layout(left, top, left + widths[i], top + mImageViews[i].getMeasuredHeight());

            if (left < paddingLeft) {
                mImageViews[i].setAlpha(1 - ((float) Math.abs(Math.min(left - paddingLeft, 0))) / widths[i]);
            } else {
                mImageViews[i].setAlpha(1f);
            }
        }

        mLastKnownPositionOffset = positionOffset;
        mUpdatingPositions = false;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Must measure with an exact width");
        }

        int childHeight = heightSize;
        int minHeight = getMinHeight();
        int padding = 0;
        padding = getPaddingTop() + getPaddingBottom();
        childHeight -= padding;

        final int childWidthSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * 0.8f), MeasureSpec.AT_MOST);
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);

        for (ImageView iv : mImageViews) {
            iv.measure(childWidthSpec, childHeightSpec);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else {
            int textHeight = mImageViews[mImageViews.length / 2].getMeasuredHeight();
            setMeasuredDimension(widthSize, Math.max(minHeight, textHeight + padding));
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mPager != null) {
            final float offset = mLastKnownPositionOffset >= 0 ? mLastKnownPositionOffset : 0;
            updateDrawablePositions(mLastKnownCurrentPage, offset, true);
        }
    }


    int getMinHeight() {
        int minHeight = 0;
        final Drawable bg = getBackground();
        if (bg != null) {
            minHeight = bg.getIntrinsicHeight();
        }
        return minHeight;
    }

    private class PageListener extends DataSetObserver implements ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {
        private int mScrollState;


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset > 0.5f) {
                position++;
            }
            updateDrawablePositions(position, positionOffset, false);
        }


        @Override
        public void onPageSelected(int position) {
        }


        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }


        @Override
        public void onAdapterChanged(PagerAdapter oldAdapter, PagerAdapter newAdapter) {
            updateAdapter(oldAdapter, newAdapter);
        }


        @Override
        public void onChanged() {
            updateImages(mPager.getCurrentItem(), mPager.getAdapter());

            final float offset = mLastKnownPositionOffset >= 0 ? mLastKnownPositionOffset : 0;
            updateDrawablePositions(mPager.getCurrentItem(), offset, true);
        }
    }
}
