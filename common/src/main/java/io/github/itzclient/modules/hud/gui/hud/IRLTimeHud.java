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
import io.github.itzclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IRLTimeHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "irltimehud");

    // --- Settings for this module ---
    private final StringOption format = new StringOption("dateformat", "HH:mm:ss", this::updateDateTimeFormatter);
    
    // --- Caching variables for optimization ---
    private DateTimeFormatter formatter;
    private String timeString = "";
    private int tickCounter = 0;
    private boolean hasError = false;

    public IRLTimeHud() {
        super();
        updateDateTimeFormatter(format.get());
    }

    private void updateDateTimeFormatter(String value) {
        try {
            // Attempt to create a formatter from the user's string.
            this.formatter = DateTimeFormatter.ofPattern(value);
            this.hasError = false;
        } catch (IllegalArgumentException e) {
            // If the user enters an invalid format, we catch the error.
            this.hasError = true;
            this.formatter = null;
        }
    }
    
    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        // Update the time string once per second (every 20 ticks).
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;

            if (hasError || formatter == null) {
                timeString = "Invalid Format";
            } else {
                timeString = formatter.format(LocalDateTime.now());
            }
        }
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(format);
        return options;
    }

    @Override
    public String getValue() {
        // Return the cached time string every frame.
        return this.timeString;
    }

    @Override
    public String getPlaceholder() {
        if (hasError || formatter == null) {
            return "Invalid Format";
        }
        // Use a fixed date for a consistent placeholder.
        return formatter.format(LocalDateTime.of(2025, Month.JANUARY, 1, 12, 34, 56));
    }
}