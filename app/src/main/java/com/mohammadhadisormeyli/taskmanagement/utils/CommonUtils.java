package com.mohammadhadisormeyli.taskmanagement.utils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.mohammadhadisormeyli.taskmanagement.R;

import java.util.Locale;

public final class CommonUtils {


    public static String[] getPermissionList() {
        String[] permissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };
        }
        return permissions;
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
//        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public static GradientDrawable getGradientDrawable(int shape, GradientDrawable.Orientation orientation, int startColor, int centerColor, int endColor) {
        GradientDrawable gradientDrawable = new GradientDrawable(orientation, new int[]{startColor, centerColor, endColor});
        gradientDrawable.setShape(shape);
        return gradientDrawable;
    }

    public static LayerDrawable getBottomOvalDrawable(GradientDrawable.Orientation orientation, int startColor, int centerColor, int endColor) {
        GradientDrawable gradientDrawable = getGradientDrawable(GradientDrawable.OVAL, orientation, startColor, centerColor, endColor);
        int inset = (int) ScreenUtils.toDp(-72);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
        layerDrawable.setLayerInset(0, inset, inset, inset, 0);
        return layerDrawable;
    }

    public static Intent openIntentBaseFile(String file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = file.substring(file.indexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);

        intent.setDataAndType(Uri.parse(file), type);
        return intent;
    }

    public static ColorStateList getSwitchTrackColorStateList(Context context, int checkedColor) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        states[i] = new int[]{-android.R.attr.state_enabled};
        colors[i] = ContextCompat.getColor(context, R.color.pure_white);
        i++;

        states[i] = new int[]{android.R.attr.state_checked};
        colors[i] = ContextCompat.getColor(context, checkedColor);
        i++;

        states[i] = new int[0];
        colors[i] = ContextCompat.getColor(context, R.color.grey_500);

        return new ColorStateList(states, colors);
    }

    public static ColorStateList getCheckBoxColorStateList(int uncheckedColor, int checkedColor) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}  // checked
                },
                new int[]{
                        uncheckedColor,
                        checkedColor
                }
        );
        return colorStateList;
    }
}
