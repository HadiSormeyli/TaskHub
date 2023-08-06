package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes;

import android.os.Parcel;
import android.os.Parcelable;


public final class FactoryPalette implements Palette {

    public static final Parcelable.Creator<FactoryPalette> CREATOR = new Parcelable.Creator<FactoryPalette>() {
        @Override
        public FactoryPalette createFromParcel(Parcel in) {
            final FactoryPalette state = new FactoryPalette();
            state.readFromParcel(in);
            return state;
        }


        @Override
        public FactoryPalette[] newArray(int size) {
            return new FactoryPalette[size];
        }
    };
    private String mName;
    /**
     * The Id of this palette.
     */
    private String mPaletteId;
    /**
     * The colors in this palette.
     */
    private int[] mValues;
    /**
     * The number of columns to use for the layout of this palette.
     */
    private int mColumns;

    /**
     * Private constructor for unparcelling;
     */
    private FactoryPalette() {
    }


    /**
     * Build a new palette with a custom {@link ColorFactory}.
     *
     * @param id            An identifier for this palette.
     * @param name          The name of the palette.
     * @param colorProvider An instance of {@link ColorFactory} that can return a color for each index.
     * @param count         The number of colors to generate in this palette.
     * @param columns       The number of columns to use in the layout
     */
    public FactoryPalette(String id, String name, ColorFactory colorProvider, int count, int columns) {
        mPaletteId = id;
        mName = name;
        int[] values = new int[count];
        for (int i = 0; i < count; ++i) {
            values[i] = colorProvider.colorAt(i, count);
        }
        mValues = values;
        mColumns = columns;
    }


    /**
     * Build a new palette with a custom {@link ColorFactory}.
     *
     * @param id            An identifier for this palette.
     * @param name          The name of the palette.
     * @param colorProvider An instance of {@link ColorFactory} that can return a color for each index.
     * @param count         The number of colors to generate in this palette.
     */
    public FactoryPalette(String id, String name, ColorFactory colorProvider, int count) {
        this(id, name, colorProvider, count, (int) Math.floor(Math.sqrt(count)));
    }

    public int[] getValues() {
        return mValues;
    }

    @Override
    public String name() {
        return mName;
    }


    @Override
    public String id() {
        return mPaletteId;
    }


    @Override
    public int numberOfColors() {
        return mValues.length;
    }


    @Override
    public int colorAt(int index) {
        return mValues[index];
    }


    @Override
    public String nameOfColorAt(int index) {
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
        dest.writeString(mName);
        dest.writeIntArray(mValues);
        dest.writeInt(mColumns);
    }


    public void readFromParcel(Parcel in) {
        mName = in.readString();
        mValues = in.createIntArray();
        mColumns = in.readInt();
    }
}
