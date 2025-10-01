/*
 * Copyright © 2025 itzmojahide2 & Contributors
 *
 * This file is part of ItzClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */
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
