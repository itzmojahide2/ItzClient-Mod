package io.github.itzclient.AxolotlClientConfig.impl.options;

public class IntegerOption extends OptionBase<Integer> {
    private int value;
    public final int min;
    public final int max;

    public IntegerOption(String name, Integer defaultValue, int min, int max) {
        this(name, defaultValue, null, min, max);
    }

    public IntegerOption(String name, Integer defaultValue, ChangeListener<Integer> listener, int min, int max) {
        super(name, defaultValue, listener);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public void set(Integer value) {
        int clamped = Math.max(min, Math.min(value, max));
        if (this.value != clamped) {
            this.value = clamped;
            onSet(clamped);
        }
    }

    @Override
    public String toSerializedValue() {
        return String.valueOf(value);
    }

    @Override
    public void fromSerializedValue(String s) {
        set(Integer.parseInt(s));
    }

    @Override
    public String getWidgetIdentifier() {
        return "integer";
    }
                              }
