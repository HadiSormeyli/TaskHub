package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * A palette that gets colors and names from arrays. If no column number is specified, this class uses the next integer below the square root fo the number of
 * colors.
 *
 * @author Marten Gajda
 */
public final class ArrayPalette implements Palette {

    public static final Parcelable.Creator<ArrayPalette> CREATOR = new Parcelable.Creator<ArrayPalette>() {
        @Override
        public ArrayPalette createFromParcel(Parcel in) {
            return new ArrayPalette(in.readString(), in.readString(), in.createIntArray(), in.readInt(), in.createStringArray());
        }


        @Override
        public ArrayPalette[] newArray(int size) {
            return new ArrayPalette[size];
        }
    };


    private final String mPaletteName;

    private final String mPaletteId;

    private final int[] mColors;

    private final String[] mColorNames;
    private final int mColumns;

    public ArrayPalette(String id, String paletteName, int[] colors, int columns, String[] names) {
        mPaletteId = id;
        mPaletteName = paletteName;
        mColors = colors;
        mColorNames = names;
        mColumns = columns;
    }

    public ArrayPalette(String id, String paletteName, int[] colors, String[] names) {
        this(id, paletteName, colors, (int) Math.floor(Math.sqrt(colors.length)), names);
    }

    public ArrayPalette(String id, String paletteName, int[] colors, int columns) {
        this(id, paletteName, colors, columns, null);
    }

    public ArrayPalette(String id, String paletteName, int[] colors) {
        this(id, paletteName, colors, (int) Math.floor(Math.sqrt(colors.length)), null);
    }

    public static ArrayPalette fromResources(Resources resources, String id, int paletteName, int colorArray, int columns, int colorNameArray) {
        return new ArrayPalette(id, resources.getString(paletteName), resources.getIntArray(colorArray), columns, resources.getStringArray(colorNameArray));
    }

    public static ArrayPalette fromResources(Resources resources, String id, int paletteName, int colorArray, int colorNameArray) {
        return new ArrayPalette(id, resources.getString(paletteName), resources.getIntArray(colorArray), resources.getStringArray(colorNameArray));
    }

    public static ArrayPalette fromResources(Resources resources, String id, int paletteName, int colorArray) {
        return new ArrayPalette(id, resources.getString(paletteName), resources.getIntArray(colorArray));
    }

    public static ArrayPalette fromResources(Context context, String id, int paletteName, int colorArray, int columns, int colorNameArray) {
        return fromResources(context.getResources(), id, paletteName, colorArray, columns, colorNameArray);
    }

    public static ArrayPalette fromResources(Context context, String id, int paletteName, int colorArray, int colorNameArray) {
        return fromResources(context.getResources(), id, paletteName, colorArray, colorNameArray);
    }

    public static ArrayPalette fromResources(Context context, String id, int paletteName, int colorArray) {
        return fromResources(context.getResources(), id, paletteName, colorArray);
    }


    @Override
    public String name() {
        return mPaletteName;
    }


    @Override
    public String id() {
        return mPaletteId;
    }


    @Override
    public int numberOfColors() {
        return mColors.length;
    }


    @Override
    public int colorAt(int index) {
        return mColors[index];
    }


    @Override
    public String nameOfColorAt(int index) {
        if (mColorNames != null) {
            return mColorNames[index];
        }
        return null;
    }


    @Override
    public int numberOfColumns() {
        return mColumns;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPaletteId);
        dest.writeString(mPaletteName);
        dest.writeIntArray(mColors);
        dest.writeInt(mColumns);
        dest.writeStringArray(mColorNames);
    }
}
