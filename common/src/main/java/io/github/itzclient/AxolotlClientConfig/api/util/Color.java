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
```--- END OF FILE ---

**`ColorOption.java`**
**Location:** `common/src/main/java/io/github/itzclient/AxolotlClientConfig/impl/options/ColorOption.java`
--- START OF FILE ---
```java
package io.github.itzclient.AxolotlClientConfig.impl.options;

import io.github.itzclient.AxolotlClientConfig.api.util.Color;

public class ColorOption extends OptionBase<Color> {
    private Color value;

    public ColorOption(String name, Color defaultValue, ChangeListener<Color> listener) {
        super(name, defaultValue, listener);
        this.value = defaultValue;
    }
    
    public ColorOption(String name, Color defaultValue) {
        this(name, defaultValue, null);
    }
    
    public ColorOption(String name, String tooltip, Color defaultValue) {
        this(name, defaultValue, null);
    }

    @Override
    public Color get() { return value; }

    @Override
    public void set(Color value) {
        if (this.value == null || !this.value.equals(value)) {
            this.value = value;
            onSet(value);
        }
    }

    @Override
    public String toSerializedValue() { return String.valueOf(value.toInt()); }

    @Override
    public void fromSerializedValue(String s) {
        set(new Color(Integer.parseInt(s)));
    }

    @Override
    public String getWidgetIdentifier() { return "color"; }
  }
