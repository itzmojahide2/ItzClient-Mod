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

import io.github.itzclient.bridge.events.Events;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TPSHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "tpshud");
    private static final NumberFormat FORMATTER = new DecimalFormat("#0.00");
    
    // --- Caching variables for calculation ---
    private long lastTickTime = -1;
    private long lastUpdateTime = -1;
    private double tps = -1;
    private String tpsString = "20.00 TPS"; // Default display value

    @Override
    public void init() {
        // Listen to the server's time update packets
        Events.UPDATE_TIME.register(ticks -> {
            if (lastTickTime < 0) {
                lastTickTime = ticks;
                lastUpdateTime = System.nanoTime();
                return;
            }

            long currentTime = System.nanoTime();
            // Time elapsed in milliseconds
            double elapsedMillis = (currentTime - lastUpdateTime) / 1.0E6;
            // Ticks passed since last update
            int passedTicks = (int) (ticks - lastTickTime);
            
            if (passedTicks > 0) {
                // Calculate Milliseconds Per Tick (MSPT)
                double mspt = elapsedMillis / passedTicks;
                // Calculate TPS (capped at 20.0)
                tps = Math.min(1000.0 / mspt, 20.0);
                tpsString = FORMATTER.format(tps) + " TPS";
            }

            lastTickTime = ticks;
            lastUpdateTime = currentTime;
        });
        
        // Reset TPS when disconnecting from a world
        Events.DISCONNECT.register(() -> {
            lastTickTime = -1;
            lastUpdateTime = -1;
            tps = -1;
            tpsString = "20.00 TPS";
        });
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        if (client.br$isLocalServer()) {
            return "20.00 TPS (SP)"; // Singleplayer always runs at 20 TPS
        }
        return tpsString;
    }

    @Override
    public String getPlaceholder() {
        return "20.00 TPS";
    }
}