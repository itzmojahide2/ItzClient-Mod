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
package io.github.itzclient.modules.hud.gui.hud.simple;

import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.itzclient.bridge.key.AxoKeybinding;
import io.github.itzclient.bridge.key.AxoKeys;
import io.github.itzclient.bridge.util.AxoI18n;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;
import io.github.itzclient.util.options.ForceableBooleanOption;
import lombok.Getter;

import java.util.List;

public class ToggleSprintHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "togglesprint");

    // --- Settings for this module ---
    public final ForceableBooleanOption toggleSneak = new ForceableBooleanOption("toggleSneak", false);
    private final BooleanOption toggleSprint = new BooleanOption("toggleSprint", false);
    private final StringOption placeholder = new StringOption("placeholder", "No keys pressed");

    // --- Keybinds for toggling ---
    private final AxoKeybinding sprintToggleKey = AxoKeybinding.create(AxoKeys.KEY_K, "key.toggleSprint", "category.itzclient");
    private final AxoKeybinding sneakToggleKey = AxoKeybinding.create(AxoKeys.KEY_I, "key.toggleSneak", "category.itzclient");

    // These options store the toggled state and are saved in the config
    @Getter
    private final BooleanOption sprintToggled = new BooleanOption("sprintToggled", false);
    @Getter
    private final BooleanOption sneakToggled = new BooleanOption("sneakToggled", false);

    public ToggleSprintHud() {
        super(100, 13, true); // Default size
    }

    @Override
    public void init() {
        // Register the actions for our toggle keybinds
        sprintToggleKey.br$registerOnConsumeClick(sprintToggled::toggle);
        sneakToggleKey.br$registerOnConsumeClick(sneakToggled::toggle);
    }

    @Override
    public List<Option<?>> getSaveOptions() {
        List<Option<?>> options = super.getSaveOptions();
        // Ensure the toggle states are saved with the user's profile
        options.add(sprintToggled);
        options.add(sneakToggled);
        return options;
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(toggleSprint);
        options.add(toggleSneak);
        options.add(placeholder);
        return options;
    }

    @Override
    public String getValue() {
        if (client.br$getPlayer() == null) {
            return getPlaceholder();
        }

        if (client.br$getKeybinds().br$getSneakKeybind().br$isPressed()) {
            return AxoI18n.translate("sneaking_pressed");
        }

        if (client.br$getKeybinds().br$getSprintKeybind().br$isPressed()) {
            return AxoI18n.translate("sprinting_pressed");
        }

        if (toggleSneak.get() && sneakToggled.get()) {
            return AxoI18n.translate("sneaking_toggled");
        }

        if (toggleSprint.get() && sprintToggled.get()) {
            return AxoI18n.translate("sprinting_toggled");
        }

        return placeholder.get();
    }

    @Override
    public String getPlaceholder() {
        return AxoI18n.translate("sprinting_toggled");
    }
}