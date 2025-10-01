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
