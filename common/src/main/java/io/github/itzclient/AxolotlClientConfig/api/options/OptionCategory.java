package io.github.itzclient.AxolotlClientConfig.api.options;

import java.util.List;

public interface OptionCategory extends Option<Object> {
    
    static OptionCategory create(String name) {
        // We will use a simplified implementation for now.
        // The original library had a more complex factory here.
        return new io.github.itzclient.AxolotlClientConfig.impl.options.OptionCategoryImpl(name);
    }
    
    List<Option<?>> getOptions();
    
    List<OptionCategory> getSubCategories();

    void add(Option<?>... options);

    void add(OptionCategory category);
    
    void add(OptionCategory category, boolean in_list);
}
