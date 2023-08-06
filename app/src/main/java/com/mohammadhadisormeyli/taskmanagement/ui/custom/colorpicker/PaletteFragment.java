package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;

import androidx.fragment.app.Fragment;

import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes.Palette;


public final class PaletteFragment extends Fragment implements OnItemClickListener {

    private Palette mPalette;

    private PaletteGridAdapter mAdapter;


    public void setPalette(Palette palette) {
        mPalette = palette;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        /*
         * TODO: build the layout programmatically to get rid of the resources, so we can distribute this in a single jar
         */
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.dmfs_colorpickerdialog_palette_grid, container, false);
        final GridView gridview = (GridView) rootView.findViewById(android.R.id.content);

        mAdapter = new PaletteGridAdapter(getActivity(), mPalette);
        gridview.setAdapter(mAdapter);
        gridview.setOnItemClickListener(this);
        gridview.setNumColumns(mAdapter.getNumColumns());


        gridview.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int parentHeight = rootView.getHeight() - rootView.getPaddingTop() - rootView.getPaddingBottom();
            int parentWidth = rootView.getWidth() - rootView.getPaddingLeft() - rootView.getPaddingRight();

            int gridWidth = Math.min(parentWidth, parentHeight);

            int columnSpacing;
            columnSpacing = gridview.getHorizontalSpacing() * (mAdapter.getNumColumns() - 1);

            int columnWidth = (gridWidth - columnSpacing) / mAdapter.getNumColumns();

            int actualGridWidth = mAdapter.getNumColumns() * columnWidth + columnSpacing;

            if (actualGridWidth < gridWidth - 1) {
                int padding = (gridWidth - actualGridWidth) / 2;
                if (padding > 0) {
                    gridview.setPadding(padding, padding, padding, padding);

                }
            } else {
                gridview.setPadding(0, 0, 0, 0);
            }

            gridview.setColumnWidth(columnWidth);

            ViewGroup.LayoutParams params = gridview.getLayoutParams();
            if (params == null || params.height != gridWidth) // avoid unnecessary updates
            {
                LayoutParams lparams = new LayoutParams(gridWidth, gridWidth);
                gridview.setLayoutParams(lparams);
            }
        });
        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> gridView, View View, int position, long id) {
        // pass the click event to the parent fragment
        Fragment parent = getParentFragment();
        if (parent instanceof OnColorSelectedListener) {
            OnColorSelectedListener listener = (OnColorSelectedListener) parent;
            listener.onColorSelected(mPalette.colorAt(position), mPalette.id(), mPalette.nameOfColorAt(position), mPalette.name());
        }
    }


    public interface OnColorSelectedListener {
        void onColorSelected(int color, String paletteId, String colorName, String paletteName);
    }
}
