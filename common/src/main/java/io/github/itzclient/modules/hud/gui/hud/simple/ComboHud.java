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

import io.github.itzclient.bridge.Platform;
import io.github.itzclient.bridge.events.Events;
import io.github.itzclient.bridge.util.AxoI18n;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

public class ComboHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "combohud");

    // --- State variables for tracking the combo ---
    private long lastHitTime = 0;
    private int lastTargetId = -1;
    private int comboCount = 0;

    @Override
    public void init() {
        // Listen for when the player attacks an entity
        Events.PLAYER_ATTACK.register((player, attackedEntity) -> {
            // Check if we are hitting the same target as before or starting a new combo
            if (attackedEntity.br$getNetId() == lastTargetId) {
                comboCount++;
            } else {
                // New target, start a new combo from 1
                lastTargetId = attackedEntity.br$getNetId();
                comboCount = 1;
            }
            lastHitTime = Platform.getMeasuringTimeMs();
        });

        // Listen for when the player takes damage
        Events.PLAYER_HURT.register((player, attacker) -> {
            // If the player gets hit, their combo is broken
            if (client.br$getPlayer() != null && player.br$getUuid().equals(client.br$getPlayer().br$getUuid())) {
                comboCount = 0;
                lastTargetId = -1;
            }
        });
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        // If 2 seconds (2000 ms) have passed since the last hit, reset the combo
        if (lastHitTime + 2000 < Platform.getMeasuringTimeMs()) {
            comboCount = 0;
        }

        if (comboCount == 0) {
            return AxoI18n.translate("combocounter.no_hits");
        }
        if (comboCount == 1) {
            return AxoI18n.translate("combocounter.one_hit");
        }
        return AxoI18n.translate("combocounter.hits", comboCount);
    }

    @Override
    public String getPlaceholder() {
        return AxoI18n.translate("combocounter.hits", 5);
    }
}