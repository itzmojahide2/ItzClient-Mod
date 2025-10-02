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

    public final static AxoIdentifier ID = AxoIdentifier.of("itzclient", "tpshud");
    private final static NumberFormat FORMATTER = new DecimalFormat("#0.00");
    private long lastTick = -1;
    private long lastUpdate = -1;
    private double tps = -1;
    private String tpsString = "20.00 TPS";

    @Override
    public void init() {
        Events.UPDATE_TIME.register(ticks -> {
            if (lastTick < 0) {
                lastTick = ticks;
                lastUpdate = System.nanoTime();
                return;
            }
            long time = System.nanoTime();
            double elapsedMilli = (time - lastUpdate) / 1000000d;
            int passedTicks = (int) (ticks - lastTick);
            if (passedTicks > 0) {
                double mspt = elapsedMilli / passedTicks;
                tps = Math.min(1000 / mspt, 20);
                tpsString = FORMATTER.format(tps) + " TPS";
            }
            lastTick = ticks;
            lastUpdate = time;
        });
        Events.DISCONNECT.register(() -> {
            lastTick = -1;
            lastUpdate = -1;
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
            return "20.00 TPS (SP)";
        }
        return tpsString;
    }

    @Override
    public String getPlaceholder() {
        return "20.00 TPS";
    }
}