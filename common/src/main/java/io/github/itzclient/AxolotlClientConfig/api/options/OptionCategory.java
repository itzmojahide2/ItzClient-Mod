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
