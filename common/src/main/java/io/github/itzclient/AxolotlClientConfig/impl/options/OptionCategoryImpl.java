package io.github.itzclient.AxolotlClientConfig.impl.options;

import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.api.options.OptionCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionCategoryImpl extends OptionBase<Object> implements OptionCategory {

    private final List<Option<?>> options = new ArrayList<>();
    private final List<OptionCategory> subCategories = new ArrayList<>();

    public OptionCategoryImpl(String name) {
        super(name, null, null);
    }

    @Override
    public List<Option<?>> getOptions() {
        return options;
    }

    @Override
    public List<OptionCategory> getSubCategories() {
        return subCategories;
    }

    @Override
    public void add(Option<?>... options) {
        this.options.addAll(Arrays.asList(options));
    }

    @Override
    public void add(OptionCategory category) {
        add(category, true);
    }
    
    @Override
    public void add(OptionCategory category, boolean in_list) {
        if(in_list) {
            this.options.add(category);
        }
        this.subCategories.add(category);
    }

    @Override
    public void set(Object value) {}

    @Override
    public Object get() { return null; }

    @Override
    public String toSerializedValue() { return null; }

    @Override
    public void fromSerializedValue(String s) {}

    @Override
    public String getWidgetIdentifier() { return "category"; }
}
