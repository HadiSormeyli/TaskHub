
package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.drawable.shapes.Shape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes.Palette;


public final class PaletteGridAdapter extends BaseAdapter {

    private final Palette mPalette;

    private final LayoutInflater mLayoutInflater;


    public PaletteGridAdapter(@NonNull Context context, @NonNull Palette palette) {
        mPalette = palette;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mPalette.numberOfColors();
    }


    @NonNull
    @Override
    public Object getItem(int position) {
        return mPalette.colorAt(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.dmfs_colorpickerdialog_palette_field, null);
        }

        Shape shape = new ArcShape(0, 360);
        ShapeDrawable bg = new ShapeDrawable(shape);
        bg.getPaint().setColor(mPalette.colorAt(position));

        view.setBackground(bg);

        return view;

    }


    public int getNumColumns() {
        return mPalette.numberOfColumns();
    }
}
