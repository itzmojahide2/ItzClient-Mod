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

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.bridge.entity.AxoEntity;
import io.github.itzclient.bridge.entity.AxoPlayer;
import io.github.itzclient.bridge.math.Vec3;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

public class SpeedHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "speedhud");
    private static final NumberFormat FORMATTER = new DecimalFormat("#0.00");
    private final BooleanOption horizontal = new BooleanOption("horizontal", true);
    private String speedString = "0.00 BPS";
    private int tickCounter = 0;

    @Override
    public AxoIdentifier getId() { return ID; }
    @Override
    public List<Option<?>> getConfigurationOptions() { List<Option<?>> o = super.getConfigurationOptions(); o.add(horizontal); return o; }
    @Override
    public boolean tickable() { return true; }

    @Override
    public void tick() {
        tickCounter++;
        if (tickCounter >= 4) {
            tickCounter = 0;
            AxoPlayer player = client.br$getPlayer();
            if (player == null) { this.speedString = getPlaceholder(); return; }
            AxoEntity entity = Objects.requireNonNullElse(player.br$getVehicle(), player);
            Vec3 vec = entity.br$getVelocity();
            if (horizontal.get() || (entity.br$isOnGround() && vec.y() < 0)) {
                vec = vec.y(0);
            }
            this.speedString = FORMATTER.format(vec.len() * 20) + " BPS";
        }
    }

    @Override
    public String getValue() { return this.speedString; }
    @Override
    public String getPlaceholder() { return "4.35 BPS"; }
}