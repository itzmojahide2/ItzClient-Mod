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

import io.github.itzclient.bridge.AxoMinecraftClient;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

public class FPSHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "fpshud");
    private String fpsString = "0 FPS";
    private int tickCounter = 0;

    public FPSHud() { super(); }
    @Override
    public AxoIdentifier getId() { return ID; }
    @Override
    public boolean tickable() { return true; }

    @Override
    public void tick() {
        tickCounter++;
        if (tickCounter >= 5) {
            tickCounter = 0;
            fpsString = AxoMinecraftClient.getCurrentFps() + " FPS";
        }
    }

    @Override
    public String getValue() { return fpsString; }
    @Override
    public String getPlaceholder() { return "120 FPS"; }
}