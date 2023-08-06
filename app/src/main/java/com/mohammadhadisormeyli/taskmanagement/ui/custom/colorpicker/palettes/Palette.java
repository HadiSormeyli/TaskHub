package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes;

import android.os.Parcelable;


public interface Palette extends Parcelable {
    /**
     * Returns the display name of the palette.
     *
     * @return The name of the palette.
     */
    String name();

    /**
     * Returns the id of the palette.
     *
     * @return An Id of the palette.
     */
    String id();

    /**
     * Get the number of colors in this palette.
     *
     * @return The number of colors.
     */
    int numberOfColors();

    /**
     * Get the color at the specified index.
     *
     * @param index The index of the color.
     * @return The color at <code>index</code>
     */
    int colorAt(int index);

    /**
     * Get the the name of the color at the specified index.
     *
     * @param index The index of the color.
     * @return The name of the color at <code>index</code> or <code>null</code> if the color has no name.
     */
    String nameOfColorAt(int index);

    /**
     * Get the number of columns to use when creating the layout for this palette.
     *
     * @return The number of columns.
     */
    int numberOfColumns();
}
