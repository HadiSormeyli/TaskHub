package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes.Palette;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.view.IDrawableTitlePagerAdapter;


public final class PalettesPagerAdapter extends FragmentStatePagerAdapter implements IDrawableTitlePagerAdapter {
    private final static int PREVIEW_SIZE = 32; // dp

    private final static int FACTOR = 100;
    private final Resources mResources;
    private final float mDensity;
    private final PreviewCache mCache = new PreviewCache(15);
    private final Palette[] mPalettes;


    public PalettesPagerAdapter(Resources res, FragmentManager fm, Palette... palettes) {
        super(fm);
        mPalettes = palettes;
        mResources = res;
        mDensity = res.getDisplayMetrics().density;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        PaletteFragment fragment = new PaletteFragment();
        fragment.setPalette(mPalettes[mapPosition(position)]);
        return fragment;
    }

    @Override
    public int getCount() {
        return mPalettes.length > 1 ? mPalettes.length * FACTOR : 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPalettes[mapPosition(position)].name();
    }


    private int mapPosition(int position) {
        return position % mPalettes.length;
    }


    @Override
    public Drawable getDrawableTitle(int position) {
        return mCache.get(mapPosition(position));
    }

    private final class PreviewCache extends LruCache<Integer, Drawable> {

        public PreviewCache(int maxSize) {
            super(maxSize);
        }


        @Override
        protected Drawable create(Integer key) {
            Palette palette = mPalettes[key];
            final int size = (int) (PREVIEW_SIZE * mDensity);
            final int cols = palette.numberOfColumns();

            Bitmap preview = Bitmap.createBitmap(size, size, Config.ARGB_8888);
            Canvas canvas = new Canvas(preview);

            final float spacing = 1.2f * mDensity;
            final float halfSpacing = spacing / 2;
            final float grid = (size + spacing) / cols;
            final float radius = (grid - spacing) / 2;
            Paint paint = new Paint();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);

            for (int j = 0, k = palette.numberOfColors() / cols; j < k; ++j) {
                for (int i = 0; i < cols; ++i) {
                    paint.setColor(palette.colorAt(j * cols + i) | 0xff000000);
                    canvas.drawCircle((i + 0.5f) * grid - halfSpacing, (j + 0.5f) * grid - halfSpacing, radius, paint);
                }
            }

            return new BitmapDrawable(mResources, preview);
        }
    }
}
