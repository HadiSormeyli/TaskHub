package com.mohammadhadisormeyli.taskmanagement.ui.main.home.adapter;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadhadisormeyli.taskmanagement.R;

public class StickyHeaderItemDecorator extends RecyclerView.ItemDecoration {

    private final StickyHeaderInterface mListener;
    private int mStickyHeaderHeight;
    private final float mPaddingStart;

    public StickyHeaderItemDecorator(@NonNull StickyHeaderInterface listener, float paddingStart) {
        mListener = listener;
        mPaddingStart = paddingStart;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        View topChild = parent.getChildAt(0);
        if (topChild == null) {
            return;
        }


        int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return;
        }

        int headerPos = mListener.getHeaderPositionForItem(topChildPosition);
        View currentHeader = getHeaderViewForItem(headerPos, parent);
        fixLayoutSize(parent, currentHeader);
        int contactPoint = currentHeader.getBottom();
        View childInContact = getChildInContact(parent, contactPoint, headerPos);

        if (childInContact != null && mListener.isHeader(parent.getChildAdapterPosition(childInContact))) {
            return;
        }

        drawHeader(c, currentHeader);
    }

    private View getHeaderViewForItem(int headerPosition, RecyclerView parent) {
        int layoutResId = R.layout.date_header_item;
        View header = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        mListener.bindHeaderData(header.findViewById(R.id.header_text_view), headerPosition);
        return header;
    }

    private void drawHeader(Canvas c, View header) {
        c.save();
        c.translate(mPaddingStart, header.getTop());
        header.draw(c);
        c.restore();
    }

    private View getChildInContact(RecyclerView parent, int contactPoint, int currentHeaderPos) {
        View childInContact = null;
        for (int i = 0; i < parent.getChildCount(); i++) {
            int heightTolerance = 0;
            View child = parent.getChildAt(i);

            //measure height tolerance with child if child is another header
            if (currentHeaderPos != i) {
                boolean isChildHeader = mListener.isHeader(parent.getChildAdapterPosition(child));
                if (isChildHeader) {
                    heightTolerance = mStickyHeaderHeight - child.getHeight();
                }
            }

            //add heightTolerance if child top be in display area
            int childBottomPosition;
            if (child.getTop() > 0) {
                childBottomPosition = child.getBottom() + heightTolerance;
            } else {
                childBottomPosition = child.getBottom();
            }

            if (childBottomPosition > contactPoint) {
                if (child.getTop() <= contactPoint) {
                    // This child overlaps the contactPoint
                    childInContact = child;
                    break;
                }
            }
        }
        return childInContact;
    }

    private void fixLayoutSize(ViewGroup parent, View view) {

        // Specs for parent (RecyclerView)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Specs for children (headers)
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

        view.measure(childWidthSpec, childHeightSpec);

        view.layout(0, 0, view.getMeasuredWidth(), mStickyHeaderHeight = view.getMeasuredHeight());
    }

    public interface StickyHeaderInterface {


        int getHeaderPositionForItem(int itemPosition);

        void bindHeaderData(TextView header, int headerPosition);


        boolean isHeader(int itemPosition);
    }
}
