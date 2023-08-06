package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes;

import android.graphics.Color;


public final class ColorShadeFactory implements ColorFactory {
    private final float[] mHSL = new float[]{0, 0, 0};


    public ColorShadeFactory(float hue) {
        mHSL[0] = hue;
    }


    @Override
    public int colorAt(int index, int count) {
        index++;
        count++;
        float[] hsl = mHSL;

        if (index <= count / 2) {
            hsl[1] = 1f;
            hsl[2] = index * 2f / count;
        } else {
            hsl[1] = 2f - index * 2f / count;
            hsl[2] = 1f;
        }
        return Color.HSVToColor(255, hsl);
    }
}
