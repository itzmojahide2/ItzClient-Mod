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
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.ClickInputTracker;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

import java.util.List;

public class CPSHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "cpshud");

    // --- Settings for this module ---
    private final BooleanOption fromKeybindings = new BooleanOption("cpskeybind", false);
    private final BooleanOption rmb = new BooleanOption("rightcps", false);
    
    // --- Caching variables for optimization ---
    private String cpsString = "0 CPS";
    private int tickCounter = 0;

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(fromKeybindings);
        options.add(rmb);
        return options;
    }
    
    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        // Update the display string 10 times per second (every 2 ticks)
        tickCounter++;
        if (tickCounter >= 2) {
            tickCounter = 0;
            
            final ClickInputTracker tracker = ClickInputTracker.getInstance();
            int leftClicks = fromKeybindings.get() ? tracker.leftBind.clicks() : tracker.leftMouse.clicks();
            int rightClicks = fromKeybindings.get() ? tracker.rightBind.clicks() : tracker.rightMouse.clicks();
            
            if (rmb.get()) {
                this.cpsString = leftClicks + " | " + rightClicks + " CPS";
            } else {
                this.cpsString = leftClicks + " CPS";
            }
        }
    }

    @Override
    public String getValue() {
        return this.cpsString;
    }

    @Override
    public String getPlaceholder() {
        return rmb.get() ? "10 | 12 CPS" : "10 CPS";
    }
}
