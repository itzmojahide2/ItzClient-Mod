package io.github.itzclient.AxolotlClientConfig.api.options;

// This is a simplified version of the Option interface from the library.
public interface Option<T> {
    String getName();
    T get();
    void set(T value);
    String getTooltip();
    String toSerializedValue();
    void fromSerializedValue(String s);
}
