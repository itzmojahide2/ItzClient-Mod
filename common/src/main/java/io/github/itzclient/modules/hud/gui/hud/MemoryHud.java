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
package io.github.itzclient.modules.hud.gui.hud;

import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.api.util.Color;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoI18n;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.itzclient.modules.hud.gui.entry.TextHudEntry;
import io.github.itzclient.modules.hud.gui.layout.AnchorPoint;
import io.github.itzclient.modules.hud.gui.layout.Justification;
import io.github.itzclient.modules.hud.util.DefaultOptions;
import io.github.itzclient.modules.hud.util.DrawPosition;
import io.github.itzclient.modules.hud.util.Rectangle;
import io.github.itzclient.util.ClientColors;

import java.util.List;

public class MemoryHud extends TextHudEntry implements DynamicallyPositionable {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "memoryhud");

    // --- Settings for this module ---
    protected final EnumOption<Justification> justification = new EnumOption<>("justification", Justification.class, Justification.CENTER);
    protected final EnumOption<AnchorPoint> anchor = DefaultOptions.getAnchorPoint();
    private final ColorOption graphUsedColor = new ColorOption("graphUsedColor", ClientColors.SELECTOR_RED.withAlpha(255));
    private final ColorOption graphFreeColor = new ColorOption("graphFreeColor", ClientColors.SELECTOR_GREEN.withAlpha(255));
    private final BooleanOption showGraph = new BooleanOption("showGraph", true);
    private final BooleanOption showText = new BooleanOption("showText", false);
    private final BooleanOption showAllocated = new BooleanOption("showAllocated", false);

    // --- Caching variables for optimization ---
    private int tickCounter = 0;
    private float memoryUsagePercentage = 0.0f;
    private String memoryLine = "0MiB / 0MiB (0%)";
    private String allocationLine = "Allocated: 0MiB";

    public MemoryHud() {
        super(150, 27, true);
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        // Update memory info twice per second (every 10 ticks)
        tickCounter++;
        if (tickCounter >= 10) {
            tickCounter = 0;
            
            long maxMemory = Runtime.getRuntime().maxMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long usedMemory = totalMemory - freeMemory;

            this.memoryUsagePercentage = (float) usedMemory / (float) maxMemory;
            this.memoryLine = (usedMemory / 1024L / 1024L) + "MiB / " + (maxMemory / 1024L / 1024L) + " (" + Math.round(this.memoryUsagePercentage * 100) + "%)";
            this.allocationLine = AxoI18n.translate("allocated") + ": " + (totalMemory / 1024L / 1024L) + "MiB";
        }
    }

    @Override
    public void renderComponent(AxoRenderContext context, float delta) {
        DrawPosition pos = getPos();

        if (showGraph.get()) {
            Rectangle graph = new Rectangle(pos.x() + 5, pos.y() + 5, getBounds().width - 10, getBounds().height - 10);
            final int usagePx = (int) (graph.width() * memoryUsagePercentage);
            context.br$fillRect(graph.x(), graph.y(), usagePx, graph.height(), graphUsedColor.get().toInt());
            context.br$fillRect(graph.x() + usagePx, graph.y(), graph.width() - usagePx, graph.height(), graphFreeColor.get().toInt());
            context.br$outlineRect(graph, ClientColors.BLACK);
        }

        if (showText.get()) {
            int yOffset = showAllocated.get() ? -4 : 0;
            context.br$drawString(
                memoryLine,
                pos.x() + justification.get().getXOffset(context.br$getFont().br$getWidth(memoryLine), getWidth() - 4) + 2,
                pos.y() + (Math.round((float) getHeight() / 2) - 4) + yOffset,
                textColor.get().toInt(),
                shadow.get()
            );

            if (showAllocated.get()) {
                context.br$drawString(
                    allocationLine,
                    pos.x() + justification.get().getXOffset(context.br$getFont().br$getWidth(allocationLine), getWidth() - 4) + 2,
                    pos.y() + (Math.round((float) getHeight() / 2) - 4) + 4,
                    textColor.get().toInt(),
                    shadow.get()
                );
            }
        }
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
        // Use fixed values for the placeholder render
        memoryUsagePercentage = 0.6f; // 60%
        memoryLine = "1228MiB / 2048MiB (60%)";
        allocationLine = "Allocated: 1536MiB";
        renderComponent(context, delta);
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(justification);
        options.add(anchor);
        options.add(showGraph);
        options.add(graphUsedColor);
        options.add(graphFreeColor);
        options.add(showText);
        options.add(showAllocated);
        return options;
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public AnchorPoint getAnchor() {
        return anchor.get();
    }
}