package io.github.itzclient.AxolotlClientConfig.api.util;

// This is a simplified Color class. The original was much more complex.
public class Color {
    private int red, green, blue, alpha;
    
    public Color(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    
    public Color(int red, int green, int blue) { this(red, green, blue, 255); }
    public Color(int rgba) {
        this.alpha = (rgba >> 24) & 0xFF;
        this.red = (rgba >> 16) & 0xFF;
        this.green = (rgba >> 8) & 0xFF;
        this.blue = rgba & 0xFF;
    }

    public int getRed() { return red; }
    public int getGreen() { return green; }
    public int getBlue() { return blue; }
    public int getAlpha() { return alpha; }

    public int toInt() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    
    public Color withAlpha(int alpha) { return new Color(red, green, blue, alpha); }
    public Color immutable() { return this; }
    
    public static Color parse(String s) {
        return new Color(Integer.parseInt(s.substring(1), 16));
    }
}
