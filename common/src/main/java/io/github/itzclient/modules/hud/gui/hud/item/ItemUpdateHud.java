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
import io.github.itzclient.AxolotlClientConfig.api.util.Colors;
import io.github.itzclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.itzclient.bridge.item.AxoItemStack;
import io.github.itzclient.bridge.item.AxoItems;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.bridge.util.AxoText;
import io.github.itzclient.modules.hud.gui.entry.TextHudEntry;
import io.github.itzclient.modules.hud.util.DrawPosition;
import io.github.itzclient.util.ClientColors;
import io.github.itzclient.util.ItemUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemUpdateHud extends TextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "itemupdatehud");

    // --- Placeholders for the HUD editor ---
    private static final List<ItemUtil.TimedItemStorage> PLACEHOLDER_ADDED = List.of(
        new ItemUtil.TimedItemStorage(AxoItemStack.of(AxoItems.DIAMOND, 2), 0)
    );
    private static final List<ItemUtil.TimedItemStorage> PLACEHOLDER_REMOVED = List.of(
        new ItemUtil.TimedItemStorage(AxoItemStack.of(AxoItems.EMERALD, 3), 0)
    );

    // --- Settings for this module ---
    private final IntegerOption timeout = new IntegerOption("timeout", 6, 1, 60);
    private final ColorOption bracketColor = new ColorOption("itemupdatehud.bracket_color", Colors.DARK_GRAY);
    
    // --- State variables for tracking item changes ---
    private List<ItemUtil.ItemStorage> oldItems = new ArrayList<>();
    private ArrayList<ItemUtil.TimedItemStorage> removed;
    private ArrayList<ItemUtil.TimedItemStorage> added;

    public ItemUpdateHud() {
        super(200, 11 * 6 - 2, true);
        removed = new ArrayList<>();
        added = new ArrayList<>();
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if (client.br$getWorld() != null) {
            update();
        }
    }

    public void update() {
        // Remove items from the display list that have timed out
        this.removed = ItemUtil.removeOld(removed, timeout.get() * 1000);
        this.added = ItemUtil.removeOld(added, timeout.get() * 1000);

        // Check for newly added items
        List<ItemUtil.ItemStorage> currentItems = ItemUtil.storageFromItem(ItemUtil.getItems(client));
        ItemUtil.compare(currentItems, oldItems).stream()
            .map(ItemUtil.ItemStorage::timed)
            .forEach(stack -> {
                ItemUtil.getTimedItemFromItem(stack.stack, this.added)
                    .ifPresentOrElse(
                        item -> item.incrementTimes(stack.times),
                        () -> this.added.add(stack)
                    );
            });

        // Check for newly removed items
        ItemUtil.compare(oldItems, currentItems).stream()
            .map(ItemUtil.ItemStorage::timed)
            .forEach(stack -> {
                ItemUtil.getTimedItemFromItem(stack.stack, this.removed)
                    .ifPresentOrElse(
                        item -> item.incrementTimes(stack.times),
                        () -> this.removed.add(stack)
                    );
            });

        this.added.sort((o1, o2) -> Float.compare(o1.getPassedTime(), o2.getPassedTime()));
        this.removed.sort((o1, o2) -> Float.compare(o1.getPassedTime(), o2.getPassedTime()));
        
        // Update the old inventory state for the next tick
        oldItems = currentItems;
    }

    @Override
    public void renderComponent(AxoRenderContext context, float delta) {
        renderInternal(context, added, removed);
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
        renderInternal(context, PLACEHOLDER_ADDED, PLACEHOLDER_REMOVED);
    }
    
    private void renderInternal(AxoRenderContext context, List<ItemUtil.TimedItemStorage> currentAdded, List<ItemUtil.TimedItemStorage> currentRemoved) {
        final AxoText openBracket = AxoText.literal("[").br$color(bracketColor.get().toInt());
        final AxoText closingBracket = AxoText.literal("]").br$color(bracketColor.get().toInt());
        final int deltaY = context.br$getFont().br$getFontHeight() + 2;

        DrawPosition pos = getPos();
        int lastY = 1;
        int entryCount = 0;

        // Render added items
        for (ItemUtil.ItemStorage item : currentAdded) {
            if (entryCount++ > 5) return;
            AxoText message = AxoText.literal("+ ")
                .br$append(openBracket)
                .br$append(String.valueOf(item.times))
                .br$append(closingBracket)
                .br$append(" ")
                .br$append(item.stack.br$getHoverName());
            context.br$drawString(message, pos.x(), pos.y() + lastY, ClientColors.SELECTOR_GREEN.toInt(), shadow.get());
            lastY += deltaY;
        }

        // Render removed items
        for (ItemUtil.ItemStorage item : currentRemoved) {
            if (entryCount++ > 5) return;
            AxoText message = AxoText.literal("- ")
                .br$append(openBracket)
                .br$append(String.valueOf(item.times))
                .br$append(closingBracket)
                .br$append(" ")
                .br$append(item.stack.br$getHoverName());
            context.br$drawString(message, pos.x(), pos.y() + lastY, ClientColors.SELECTOR_RED.toInt(), shadow.get());
            lastY += deltaY;
        }
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(timeout);
        options.add(bracketColor);
        return options;
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }
}