package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes;

import android.graphics.Color;


public final class ColorLightnessFactory implements ColorFactory {
    private final float[] mHSL = new float[]{0, 0, 0};


    public ColorLightnessFactory(float hue, float saturation) {
        mHSL[0] = hue;
        mHSL[1] = saturation;
    }


    @Override
    public int colorAt(int index, int count) {
        if (count <= 1) {
            return Color.WHITE;
        }

        float[] hsl = mHSL;

        hsl[2] = (float) index / (count - 1);

        return Color.HSVToColor(255, hsl);
    }
}
