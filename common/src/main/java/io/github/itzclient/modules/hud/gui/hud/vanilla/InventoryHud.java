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
package io.github.itzclient.modules.hud.gui.hud.vanilla;

import io.github.itzclient.bridge.item.AxoItemStack;
import io.github.itzclient.bridge.item.AxoItems;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.itzclient.modules.hud.gui.entry.BoxHudEntry;
import io.github.itzclient.modules.hud.gui.layout.AnchorPoint;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InventoryHud extends BoxHudEntry implements DynamicallyPositionable {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "inventoryhud");

    // A list of items to show when in the HUD editor for placeholder purposes
    private static final List<AxoItemStack> PLACEHOLDER_ITEMS = Stream.of(
        IntStream.range(0, 9).mapToObj(x -> AxoItemStack.of(AxoItems.STONE)),
        IntStream.range(0, 9).mapToObj(x -> (AxoItemStack) null),
        Stream.of(
            AxoItemStack.of(AxoItems.DIAMOND_SWORD),
            AxoItemStack.of(AxoItems.DIAMOND_PICKAXE),
            AxoItemStack.of(AxoItems.DIAMOND_AXE),
            AxoItemStack.of(AxoItems.DIAMOND_SHOVEL),
            AxoItemStack.of(AxoItems.DIAMOND_HOE),
            null, null, null,
            AxoItemStack.of(AxoItems.ENDER_PEARL, 16)
        )
    ).flatMap(x -> x).toList();

    private static final int ITEM_SIZE = 18;
    private static final int ITEM_TILE_SIZE = 16;

    public InventoryHud() {
        // The HUD is 9 items wide and 3 items tall
        super(9 * ITEM_SIZE + 2, 3 * ITEM_SIZE + 2, true);
    }

    @Override
    public double getDefaultX() {
        return 0.5; // Center X
    }

    @Override
    public double getDefaultY() {
        return 0.76; // Lower on the screen
    }

    @Override
    public void renderComponent(AxoRenderContext context, float delta) {
        if (client.br$getPlayer() == null) {
            renderPlaceholderComponent(context, delta);
            return;
        }
        // Get the player's main inventory (excluding hotbar and armor)
        render(context, client.br$getPlayer().br$getInventory().br$getNonEquipmentItems());
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
        render(context, PLACEHOLDER_ITEMS);
    }

    private void render(AxoRenderContext graphics, List<? extends AxoItemStack> inventorySlots) {
        var pos = getPos();
        int x = pos.x() + 1;
        int y = pos.y() + 1;

        // Loop through the 27 slots of the main inventory
        for (int i = 0; i < inventorySlots.size(); i++) {
            AxoItemStack stack = inventorySlots.get(i);
            if (stack != null && !stack.br$isEmpty()) {
                // Calculate the position of the item in the grid and render it
                renderStack(graphics, x + (i % 9) * ITEM_SIZE, y + (i / 9) * ITEM_SIZE, stack);
            }
        }
    }

    private void renderStack(AxoRenderContext graphics, int x, int y, AxoItemStack itemStack) {
        // The main background for the whole HUD is drawn by BoxHudEntry.
        // This method could be used to draw individual slot backgrounds if desired.
        graphics.br$renderGuiItemModel(itemStack, x, y);
        graphics.br$renderGuiItemOverlay(itemStack, x, y, null);
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public AnchorPoint getAnchor() {
        return AnchorPoint.MIDDLE_MIDDLE;
    }
          }
