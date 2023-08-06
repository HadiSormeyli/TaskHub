package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes;

import android.graphics.Color;


public final class RainbowColorFactory implements ColorFactory {
    private final float[] mHSL = new float[]{0, 0, 0};


    public RainbowColorFactory(float saturation, float lightness) {
        mHSL[1] = saturation;
        mHSL[2] = lightness;
    }


    @Override
    public int colorAt(int index, int count) {
        count += 1;
        float[] hsl = mHSL;

        hsl[0] = index * 360f / count;

        return Color.HSVToColor(255, hsl);
    }
}
