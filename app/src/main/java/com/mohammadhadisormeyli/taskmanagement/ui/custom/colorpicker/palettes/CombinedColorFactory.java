package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes;


public final class CombinedColorFactory implements ColorFactory {
    private final ColorFactory[] mFactories;


    public CombinedColorFactory(ColorFactory... factories) {
        mFactories = factories;
    }


    @Override
    public int colorAt(int index, int count) {
        int factoryCount = mFactories.length;
        return mFactories[(index * factoryCount) / count].colorAt(index % (count / factoryCount), count / factoryCount);
    }

}
