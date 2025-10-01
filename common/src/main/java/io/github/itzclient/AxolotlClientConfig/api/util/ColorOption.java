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
