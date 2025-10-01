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
