package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes.Palette;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.view.DrawablePagerTabStrip;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.view.ViewPager;


public final class ColorPickerDialogFragment extends DialogFragment implements PaletteFragment.OnColorSelectedListener {
    private ViewPager mPager;
    private TextView mTitleView;
    private PalettesPagerAdapter mPagerAdapter;
    private Palette[] mPalettes;
    private CharSequence mTitle = null;
    private int mTitleId = 0;

    private int mSelected = 0;


    public void setPalettes(Palette... palettes) {
        mPalettes = palettes;
    }


    public void selectPaletteId(String id) {
        if (mPalettes == null || id == null) {
            return;
        }

        int index = 0;
        for (Palette palette : mPalettes) {
            if (TextUtils.equals(id, palette.id())) {
                mSelected = index;
                if (mPager != null && mPagerAdapter != null) {
                    mPager.setCurrentItem(mPagerAdapter.getCount() / 2 + mSelected);
                }
                return;
            }
            ++index;
        }
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    public void setTitle(int title) {
        mTitleId = title;
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dmfs_colorpickerdialog_fragment, container);
        mPager = view.findViewById(R.id.pager);
        mPagerAdapter = new PalettesPagerAdapter(getResources(), getChildFragmentManager(), mPalettes);
        mPagerAdapter.notifyDataSetChanged();
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mPagerAdapter.getCount() / 2 + mSelected);

        mTitleView = view.findViewById(android.R.id.title);

        if (mTitleId != 0) {
            mTitleView.setText(mTitleId);
        } else if (mTitle != null) {
            mTitleView.setText(mTitle);
        }

        DrawablePagerTabStrip titleStrip = view.findViewById(R.id.pager_title_strip);
        titleStrip.setTabIndicatorColor(R.color.indigo_a_400);
        return view;
    }


    @SuppressLint("DialogFragmentCallbacksDetector")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog result = super.onCreateDialog(savedInstanceState);
        result.requestWindowFeature(Window.FEATURE_NO_TITLE);
        result.setOnCancelListener(this);
        result.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.all_corner_bg));
        ViewCompat.setElevation(result.getWindow().getDecorView(), 24);
        return result;
    }


    @Override
    public void onColorSelected(int color, String paletteId, String colorName, String paletteName) {
        ColorDialogResultListener listener = getListener();

        if (listener != null) {
            listener.onColorChanged(color, paletteId, colorName, paletteName);
        }

        dismiss();
    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        ColorDialogResultListener listener = getListener();

        if (listener != null) {
            listener.onColorDialogCancelled();
        }
    }

    private ColorDialogResultListener getListener() {
        ColorDialogResultListener listener = null;
        Fragment parentFragment = getParentFragment();
        Activity parentActivity = getActivity();

        if (parentFragment instanceof ColorDialogResultListener) {
            listener = (ColorDialogResultListener) parentFragment;
        } else if (parentActivity instanceof ColorDialogResultListener) {
            listener = (ColorDialogResultListener) parentActivity;
        }
        return listener;
    }


    public interface ColorDialogResultListener {

        public void onColorChanged(int color, String paletteId, String colorName, String paletteName);

        public void onColorDialogCancelled();
    }
}