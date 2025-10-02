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

import io.github.itzclient.bridge.util.AxoI18n;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

public class PlayerCountHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "playercounthud");

    private String playerCountString = "0 Players";
    private int tickCounter = 0;

    @Override
    public AxoIdentifier getId() {
        return ID;
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
            if (client.br$getWorld() != null) {
                int count = client.br$getWorld().br$getPlayers().size();
                this.playerCountString = count + " " + AxoI18n.translate("players");
            } else {
                this.playerCountString = getPlaceholder();
            }
        }
    }

    @Override
    public String getValue() {
        return this.playerCountString;
    }

    @Override
    public String getPlaceholder() {
        return "10 " + AxoI18n.translate("players");
    }
}