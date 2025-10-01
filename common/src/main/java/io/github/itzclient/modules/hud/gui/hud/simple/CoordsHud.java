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
import io.github.itzclient.AxolotlClientConfig.impl.options.*;
import io.github.itzclient.bridge.math.Vec3;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoI18n;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.itzclient.modules.hud.gui.entry.TextHudEntry;
import io.github.itzclient.modules.hud.gui.layout.AnchorPoint;
import io.github.itzclient.modules.hud.util.DrawPosition;
import io.github.itzclient.util.ClientColors;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class CoordsHud extends TextHudEntry implements DynamicallyPositionable {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "coordshud");

    private final ColorOption secondColor = new ColorOption("secondtextcolor", ClientColors.WHITE);
    private final ColorOption firstColor = new ColorOption("firsttextcolor", ClientColors.SELECTOR_BLUE);
    private final IntegerOption decimalPlaces = new IntegerOption("decimalplaces", 0, val -> this.updateDecimalFormat(), 0, 15);
    private final BooleanOption minimal = new BooleanOption("minimal", false);
    private final BooleanOption biome = new BooleanOption("show_biome", false);
    private final StringOption delimiter = new StringOption("coordshud.delimiter", " ");
    private final StringOption separator = new StringOption("coordshud.separator", ", ");
    private final ColorOption separatorColor = new ColorOption("coordshud.separator.color", firstColor.getDefault());
    private final EnumOption<AnchorPoint> anchor = new EnumOption<>("anchorpoint", AnchorPoint.class, AnchorPoint.TOP_MIDDLE);
    private DecimalFormat format;
    private int tickCounter = 0;
    private String fx = "0", fy = "0", fz = "0";
    private String direction = "S";
    private String xDir = "", zDir = "--";
    private String biomeName = "Plains";

    public CoordsHud() {
        super(79, 31, true);
        updateDecimalFormat();
    }
    
    private void updateDecimalFormat() {
        StringBuilder formatBuilder = new StringBuilder("0");
        if (decimalPlaces.get() > 0) {
            formatBuilder.append(".").append("0".repeat(decimalPlaces.get()));
        }
        this.format = new DecimalFormat(formatBuilder.toString());
        this.format.setRoundingMode(RoundingMode.CEILING);
    }
    
    @Override
    public boolean tickable() { return true; }

    @Override
    public void tick() {
        tickCounter++;
        if (tickCounter >= 5) {
            tickCounter = 0;
            if (client.br$getPlayer() == null) return;
            Vec3 playerPos = client.br$getPlayer().br$getPos();
            this.fx = format.format(playerPos.x());
            this.fy = format.format(playerPos.y());
            this.fz = format.format(playerPos.z());
            double yaw = client.br$getPlayer().br$getYaw() + 180;
            int dir = getDirection(yaw);
            this.direction = getWordedDirection(dir);
            this.xDir = getXDir(dir);
            this.zDir = getZDir(dir);
            if (biome.get()) {
                this.biomeName = client.br$getWorld().br$getBiomeName(playerPos);
            }
        }
    }
    
    @Override
    public void renderComponent(AxoRenderContext context, float delta) {
        doRender(context);
    }
    
    private void doRender(AxoRenderContext context) {
        DrawPosition pos = getPos();
        int width, height;
        int xStart = pos.x() + 2;
        String del = delimiter.get();
        if (minimal.get()) {
            int currPos = xStart;
            String separatorStr = this.separator.get();
            currPos = context.br$drawString("XYZ" + del, currPos, pos.y() + 2, firstColor.get().toInt(), shadow.get());
            currPos = context.br$drawString(fx, currPos, pos.y() + 2, secondColor.get().toInt(), shadow.get());
            currPos = context.br$drawString(separatorStr, currPos, pos.y() + 2, separatorColor.get().toInt(), shadow.get());
            currPos = context.br$drawString(fy, currPos, pos.y() + 2, secondColor.get().toInt(), shadow.get());
            currPos = context.br$drawString(separatorStr, currPos, pos.y() + 2, separatorColor.get().toInt(), shadow.get());
            currPos = context.br$drawString(fz, currPos, pos.y() + 2, secondColor.get().toInt(), shadow.get());
            width = currPos - pos.x() + 2;
            height = 11;
        } else {
            int xEnd;
            int yEnd = pos.y() + 2;
            int nextX = context.br$drawString("X" + del, xStart, yEnd, firstColor.get().toInt(), shadow.get());
            xEnd = context.br$drawString(fx, nextX, yEnd, secondColor.get().toInt(), shadow.get());
            yEnd += 10;
            nextX = context.br$drawString("Y" + del, xStart, yEnd, firstColor.get().toInt(), shadow.get());
            xEnd = Math.max(xEnd, context.br$drawString(fy, nextX, yEnd, secondColor.get().toInt(), shadow.get()));
            yEnd += 10;
            nextX = context.br$drawString("Z" + del, xStart, yEnd, firstColor.get().toInt(), shadow.get());
            xEnd = Math.max(xEnd, context.br$drawString(fz, nextX, yEnd, secondColor.get().toInt(), shadow.get()));
            yEnd += 10;
            xEnd = Math.max(pos.x() + 60, xEnd + 4);
            context.br$drawString(direction, xEnd, pos.y() + 12, firstColor.get().toInt(), shadow.get());
            context.br$drawString(xDir, xEnd, pos.y() + 2, secondColor.get().toInt(), shadow.get());
            context.br$drawString(zDir, xEnd, pos.y() + 22, secondColor.get().toInt(), shadow.get());
            xEnd += 14;
            width = xEnd - pos.x();
            height = yEnd + 1 - pos.y();
        }
        if (biome.get()) {
            int bX = context.br$drawString(AxoI18n.translate("coordshud.biome") + del, xStart, height + pos.y(), firstColor.get().toInt(), shadow.get());
            width = Math.max(width + pos.x() - 1, context.br$drawString(biomeName, bX, height + pos.y(), secondColor.get().toInt(), shadow.get())) - pos.x() + 1;
            height += 10;
        }
        if (getWidth() != width || getHeight() != height) {
            setWidth(width);
            setHeight(height);
            onBoundsUpdate();
        }
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext context, float delta) { doRender(context); }
    public static int getDirection(double yaw) { yaw %= 360; if (yaw < 0) yaw += 360; int[] directions = {0, 23, 68, 113, 158, 203, 248, 293, 338, 360}; for (int i = 0; i < directions.length - 1; i++) { if (yaw >= directions[i] && yaw < directions[i + 1]) { return i + 1; } } return 1; }
    public static String getXDir(int dir) { return switch (dir) { case 3 -> "++"; case 2, 4 -> "+"; case 6, 8 -> "-"; case 7 -> "--"; default -> ""; }; }
    public static String getZDir(int dir) { return switch (dir) { case 5 -> "++"; case 4, 6 -> "+"; case 8, 2 -> "-"; case 1 -> "--"; default -> ""; }; }
    public String getWordedDirection(int dir) { return switch (dir) { case 1 -> "N"; case 2 -> "NE"; case 3 -> "E"; case 4 -> "SE"; case 5 -> "S"; case 6 -> "SW"; case 7 -> "W"; case 8 -> "NW"; default -> "?"; }; }
    @Override
    public List<Option<?>> getConfigurationOptions() { List<Option<?>> options = super.getConfigurationOptions(); options.remove(textColor); options.add(firstColor); options.add(secondColor); options.add(decimalPlaces); options.add(minimal); options.add(biome); options.add(anchor); options.add(delimiter); options.add(separator); options.add(separatorColor); return options; }
    @Override
    public AxoIdentifier getId() { return ID; }
    @Override
    public AnchorPoint getAnchor() { return anchor.get(); }
}