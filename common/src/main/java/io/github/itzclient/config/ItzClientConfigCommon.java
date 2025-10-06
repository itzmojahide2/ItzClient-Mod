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
package io.github.itzclient.config;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import lombok.Getter;

/**
 * This is the common, version-independent configuration class.
 * It only contains settings that do not rely on any Minecraft code.
 * All the rendering and game-specific options have been moved to ItzClientConfig.java in the 1.21 module.
 */
@Getter
public abstract class ItzClientConfigCommon {

    // This is the main container for all configuration options.
    protected final OptionCategory config = OptionCategory.create("config");

    // This is a truly common option, as it only affects the logger.
    public final BooleanOption debugLogOutput = new BooleanOption("debugLogOutput", false);

    // This category is used for internal settings that the user doesn't see.
    public final OptionCategory hidden = OptionCategory.create("hidden");

    protected ItzClientConfigCommon() {
        // These are required by the config system but are not visible in the UI.
        hidden.add(new BooleanOption("x", false));
        hidden.add(new BooleanOption("y", false));
        config.add(hidden);
        config.add(debugLogOutput);
    }
}