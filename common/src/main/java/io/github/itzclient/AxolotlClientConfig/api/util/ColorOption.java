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
