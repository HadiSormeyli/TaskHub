package com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes;

public interface ColorFactory {
    /**
     * Grey scale from black to white
     */
    ColorFactory GREY = new ColorLightnessFactory(0, 0);
    /**
     * Shades of red (0°).
     */
    ColorFactory RED = new ColorLightnessFactory(0, 1f);
    /**
     * Shades of orange (37°).
     */
    ColorFactory ORANGE = new ColorLightnessFactory(37f, 1f);
    /**
     * Shades of yellow (60°).
     */
    ColorFactory YELLOW = new ColorLightnessFactory(60f, 1f);
    /**
     * Shades of green (120°).
     */
    ColorFactory GREEN = new ColorLightnessFactory(120f, 1f);
    /**
     * Shades of cyan (180°).
     */
    ColorFactory CYAN = new ColorLightnessFactory(180f, 1f);
    /**
     * Shades of blue (240°).
     */
    ColorFactory BLUE = new ColorLightnessFactory(240f, 1f);
    /**
     * Shades of purple (280°).
     */
    ColorFactory PURPLE = new ColorLightnessFactory(280f, 1f);
    /**
     * Shades of pink (320°).
     */
    ColorFactory PINK = new ColorLightnessFactory(320f, 1f);
    /**
     * Rainbow colors.
     */
    ColorFactory RAINBOW = new RainbowColorFactory(1f, 1f);
    /**
     * Pastel colors (same as {@link #RAINBOW} just with a saturation of 50%).
     */
    ColorFactory PASTEL = new RainbowColorFactory(0.5f, 1f);


    int colorAt(int index, int count);

}