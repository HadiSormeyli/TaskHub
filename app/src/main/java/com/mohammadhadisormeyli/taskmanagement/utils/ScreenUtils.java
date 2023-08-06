package com.mohammadhadisormeyli.taskmanagement.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;

import com.mohammadhadisormeyli.taskmanagement.R;

public final class ScreenUtils {

    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    public static float toDp(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP
                , dp
                , Resources.getSystem().getDisplayMetrics()
        );
    }

    @ColorInt
    public static int getColorFromAttr(@AttrRes int attr, Resources.Theme theme) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorSurface, typedValue, true);
        return typedValue.data;
    }

    public static ColorStateList getChipStrokeColorStateList(Resources.Theme theme, String checkedColor) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        states[i] = new int[]{-android.R.attr.state_enabled};
        colors[i] = ScreenUtils.getColorFromAttr(R.attr.colorOnBackground, theme);
        i++;

        states[i] = new int[]{android.R.attr.state_checked};
        colors[i] = ScreenUtils.getColorFromAttr(R.attr.colorOnBackground, theme);
        i++;

        states[i] = new int[0];
        colors[i] = Color.parseColor(checkedColor);

        return new ColorStateList(states, colors);
    }

    public static ColorStateList getChipBackgroundColorStateList(Resources.Theme theme, String checkedColor) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        states[i] = new int[]{-android.R.attr.state_selected};
        colors[i] = ScreenUtils.getColorFromAttr(R.attr.colorSurface, theme);
        i++;

        states[i] = new int[]{android.R.attr.state_checked};
        colors[i] = Color.parseColor(checkedColor);
        i++;

        states[i] = new int[0];

        return new ColorStateList(states, colors);
    }
}
