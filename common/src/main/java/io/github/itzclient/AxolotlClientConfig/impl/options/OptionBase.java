package io.github.itzclient.AxolotlClientConfig.impl.options;

import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Getter
public abstract class OptionBase<T> implements Option<T> {

    private final String name;
    private final T defaultValue;
    private final ChangeListener<T> changeListener;
    private String tooltip;

    public OptionBase(String name, T defaultValue, @Nullable ChangeListener<T> changeListener) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.changeListener = changeListener;
        this.tooltip = name + ".tooltip";
    }
    
    public OptionBase(String name, T defaultValue) {
        this(name, defaultValue, null);
    }
    
    protected void onSet(T value) {
        if (changeListener != null) {
            changeListener.onSet(value);
        }
    }
    
    public interface ChangeListener<T> extends Consumer<T> {
        void onSet(T value);
        
        @Override
        default void accept(T t) {
            onSet(t);
        }
    }
}
