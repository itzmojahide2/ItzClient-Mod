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
package io.github.itzclient.modules.hud.gui.hud.item;

import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.bridge.BridgeVersion;
import io.github.itzclient.bridge.item.AxoItem;
import io.github.itzclient.bridge.item.AxoItemClass;
import io.github.itzclient.bridge.item.AxoItemStack;
import io.github.itzclient.bridge.item.AxoItems;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.TextHudEntry;
import io.github.itzclient.modules.hud.util.DrawPosition;
import io.github.itzclient.util.ItemUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ArrowHud extends TextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "arrowhud");
    
    private static final List<AxoItem> ARROW_TYPES = Stream.of(
        AxoItems.ARROW, AxoItems.TIPPED_ARROW, AxoItems.SPECTRAL_ARROW
    ).filter(Objects::nonNull).toList();
    
    private static final AxoItemStack DUMMY_ARROW = AxoItemStack.of(AxoItems.ARROW, 1);

    private final BooleanOption dynamic = new BooleanOption("dynamic", false);
    private final BooleanOption allArrowTypes = new BooleanOption("allArrowTypes", false);

    private int tickCounter = 0;
    private int arrowCount = 0;
    private AxoItemStack displayArrow = DUMMY_ARROW;

    public ArrowHud() {
        super(20, 22, true);
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        tickCounter++;
        if (tickCounter >= 10) {
            tickCounter = 0;
            if (client.br$getPlayer() != null) {
                AxoItem projectileItem = client.br$getPlayer().br$getProjectileItem();
                if (!isAllArrowTypes() && projectileItem != null) {
                    displayArrow = AxoItemStack.of(projectileItem);
                } else {
                    displayArrow = DUMMY_ARROW;
                }
                if (isAllArrowTypes()) {
                    arrowCount = ARROW_TYPES.stream().mapToInt(item -> ItemUtil.getTotal(client, item)).sum();
                } else {
                    arrowCount = ItemUtil.getTotal(client, displayArrow.br$getItem());
                }
            }
        }
    }

    @Override
    public void render(AxoRenderContext graphics, float delta) {
        if (dynamic.get() && client.br$getPlayer() != null) {
            final var mainHand = client.br$getPlayer().br$getInventory().br$getMainHand().br$getItem();
            final var offHand = client.br$getPlayer().br$getInventory().br$getOffHand().br$getItem();
            if (!mainHand.br$is(AxoItemClass.RANGED_WEAPON) && !offHand.br$is(AxoItemClass.RANGED_WEAPON)) {
                return;
            }
        }
        super.render(graphics, delta);
    }

    @Override
    public void renderComponent(AxoRenderContext graphics, float delta) {
        DrawPosition pos = getPos();
        graphics.br$renderGuiItemModel(displayArrow, pos.x() + 2, pos.y() + 2);
        graphics.br$renderGuiItemOverlay(displayArrow, pos.x() + 2, pos.y() + 2, String.valueOf(arrowCount));
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext graphics, float delta) {
        DrawPosition pos = getPos();
        graphics.br$renderGuiItemModel(DUMMY_ARROW, pos.x() + 2, pos.y() + 2);
        graphics.br$renderGuiItemOverlay(DUMMY_ARROW, pos.x() + 2, pos.y() + 2, "64");
    }

    private boolean isAllArrowTypes() {
        return BridgeVersion.version().ordinal() >= BridgeVersion.V1_16_COMBAT.ordinal() && allArrowTypes.get();
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(dynamic);
        if (BridgeVersion.version().ordinal() >= BridgeVersion.V1_16_COMBAT.ordinal()) {
            options.add(allArrowTypes);
        }
        return options;
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }
}