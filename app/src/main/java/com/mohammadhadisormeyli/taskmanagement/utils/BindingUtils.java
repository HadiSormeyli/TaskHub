package com.mohammadhadisormeyli.taskmanagement.utils;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.BindingAdapter;

import com.google.android.material.textview.MaterialTextView;
import com.mohammadhadisormeyli.taskmanagement.model.SubTask;

import java.util.Date;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

public final class BindingUtils {

    @SuppressLint("ResourceAsColor")
    @BindingAdapter(value = {"done_subtasks", "progressbar_color"})
    public static void setDoneSubTasks(CircleProgressView circularProgressBar, List<SubTask> subTasks, String progressbarColor) {

        circularProgressBar.setBarColor(Color.parseColor(progressbarColor));
        circularProgressBar.setRimColor(CommonUtils.adjustAlpha(Color.parseColor(progressbarColor), 0.3f));

        if (subTasks == null || subTasks.size() == 0)
            return;

        circularProgressBar.setMaxValue(subTasks.size());

        int progress = 0;
        for (SubTask subTask : subTasks) {
            if (subTask.isDone()) progress++;
        }
        circularProgressBar.setValueAnimated(progress);
    }

    @BindingAdapter(value = {"opacity_backgroundColor", "opacity"}, requireAll = false)
    public static void setAlphaBackgroundColor(View view, String color, float opacity) {
        if(color != null) {
            opacity = (opacity == 0) ? 1f : opacity;
            int colorInt = CommonUtils.adjustAlpha(Color.parseColor(color), opacity);
            if (view instanceof ImageView) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                    colorInt = CommonUtils.adjustAlpha(colorInt, 0.2f);
                    ((ImageView) view).setImageTintList(ColorStateList.valueOf(Color.parseColor(color)));
                    view.setBackgroundColor(colorInt);
                } else {
                    view.setBackground(CommonUtils.getGradientDrawable(GradientDrawable.RECTANGLE
                            , GradientDrawable.Orientation.TL_BR
                            , colorInt, CommonUtils.adjustAlpha(colorInt, 0.65f), colorInt));
                }
            } else if (view instanceof TextView && opacity == 1)
                ((TextView) view).setTextColor(colorInt);
            else
                view.setBackgroundTintList(ColorStateList.valueOf(colorInt));
        }
    }

    @BindingAdapter("drawableBackgroundColor")
    public static void setDrawableBackgroundColor(MaterialTextView textView, String color) {
        textView.getCompoundDrawablesRelative()[0].setTint(Color.parseColor(color));
    }

    @SuppressLint("ResourceAsColor")
    @BindingAdapter(value = {"startDate", "endDate"})
    public static void setTimes(TextView textView, Date startDate, Date endDate) {
        textView.setText(
                DateUtils.dateToString(DateUtils.TIME_FORMAT, startDate).replaceAll("-", "/") + " - " +
                        DateUtils.dateToString(DateUtils.TIME_FORMAT, endDate).replaceAll("-", "/")
        );
    }
}
