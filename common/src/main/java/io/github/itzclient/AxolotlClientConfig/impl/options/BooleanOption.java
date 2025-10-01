package io.github.itzclient.AxolotlClientConfig.impl.options;

public class BooleanOption extends OptionBase<Boolean> {
    private boolean value;

    public BooleanOption(String name, Boolean defaultValue, ChangeListener<Boolean> changeListener) {
        super(name, defaultValue, changeListener);
        this.value = defaultValue;
    }

    public BooleanOption(String name, Boolean defaultValue) {
        this(name, defaultValue, null);
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public void set(Boolean value) {
        if (this.value != value) {
            this.value = value;
            onSet(value);
        }
    }

    public void toggle() {
        set(!get());
    }

    @Override
    public String toSerializedValue() {
        return String.valueOf(value);
    }

    @Override
    public void fromSerializedValue(String s) {
        set(Boolean.parseBoolean(s));
    }

    @Override
    public String getWidgetIdentifier() {
        return "boolean";
    }
}
