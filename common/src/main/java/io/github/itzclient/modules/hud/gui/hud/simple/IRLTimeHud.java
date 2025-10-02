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

    private final StringOption format = new StringOption("dateformat", "HH:mm:ss", this::updateDateTimeFormatter);
    private DateTimeFormatter formatter;
    private boolean hasError = false;
    private String timeString = "";
    private int tickCounter = 0;

    public IRLTimeHud() {
        super();
        updateDateTimeFormatter(format.get());
    }

    private void updateDateTimeFormatter(String value) {
        try {
            this.formatter = DateTimeFormatter.ofPattern(value);
            this.hasError = false;
        } catch (IllegalArgumentException e) {
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
        return this.timeString;
    }

    @Override
    public String getPlaceholder() {
        if (hasError || formatter == null) {
            return "Invalid Format";
        }
        return formatter.format(LocalDateTime.of(2025, Month.JANUARY, 1, 12, 34, 56));
    }
}