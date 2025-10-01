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
import io.github.itzclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.itzclient.modules.hud.gui.entry.TextHudEntry;
import io.github.itzclient.modules.hud.gui.layout.AnchorPoint;
import io.github.itzclient.modules.hud.util.DrawPosition;
import io.github.itzclient.util.ClientColors;

import java.util.List;

public class CompassHud extends TextHudEntry implements DynamicallyPositionable {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "compasshud");

    // --- Settings for this module ---
    private final IntegerOption widthOption = new IntegerOption("width", 240, this::updateWidth, 100, 800);
    private final ColorOption lookingBoxColor = new ColorOption("lookingbox", new Color(0x80000000));
    private final ColorOption degreesColor = new ColorOption("degreescolor", new Color(-1));
    private final ColorOption majorIndicatorColor = new ColorOption("majorindicator", new Color(-1));
    private final ColorOption minorIndicatorColor = new ColorOption("minorindicator", new Color(0xCCFFFFFF));
    private final ColorOption cardinalColor = new ColorOption("cardinalcolor", ClientColors.WHITE);
    private final ColorOption semiCardinalColor = new ColorOption("semicardinalcolor", new Color(0xFFAAAAAA));
    private final BooleanOption showDegrees = new BooleanOption("showdegrees", true);

    public CompassHud() {
        super(240, 33, false);
    }

    private void updateWidth(int newWidth) {
        setWidth(newWidth);
        onBoundsUpdate();
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    private static Indicator getIndicator(int degrees) {
        if (degrees % 90 == 0) return Indicator.CARDINAL;
        if (degrees % 45 == 0) return Indicator.SEMI_CARDINAL;
        return Indicator.SMALL;
    }

    private static String getCardString(Indicator indicator, int degrees) {
        if (indicator == Indicator.CARDINAL) {
            return switch (degrees) { case 0 -> "N"; case 90 -> "E"; case 180 -> "S"; case 270 -> "W"; default -> "?"; };
        }
        return switch (degrees) { case 45 -> "NE"; case 135 -> "SE"; case 225 -> "SW"; case 315 -> "NW"; default -> "?"; };
    }

    @Override
    public void renderComponent(AxoRenderContext context, float delta) {
        if (client.br$getPlayer() == null) {
            renderCompass(context, 180); // Render placeholder if not in-game
        } else {
            renderCompass(context, client.br$getPlayer().br$getYaw() + 180);
        }
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
        renderCompass(context, 180);
    }

    private void renderCompass(AxoRenderContext context, float yaw) {
        float halfWidth = getWidth() / 2f;
        float degrees = yaw % 360;
        if (degrees < 0) degrees += 360;

        float start = degrees - 150 + 360;
        int startIndicator = ((int) (start + 8) / 15) * 15;
        int amount = 21;
        int dist = getWidth() / amount;
        
        DrawPosition pos = getPos();
        int x = pos.x();
        int y = pos.y() + 1;

        context.br$fillRect(x + (int) halfWidth - 1, y, 3, 11, lookingBoxColor.get());

        if (showDegrees.get()) {
            context.br$drawCenteredString(String.valueOf((int) degrees), x + (int) halfWidth, y + 20, degreesColor.get(), shadow.get());
        }

        float shift = (startIndicator - start) / 15f * dist;

        context.br$pushMatrix();
        context.br$translateMatrix(shift, 0, 0);

        for (int i = 0; i < amount; i++) {
            int d = (startIndicator + i * 15) % 360;
            int innerX = x + dist * (i + 1);
            Indicator indicator = getIndicator(d);
            
            float trueDist = ((i + 1) * dist) - shift;
            float targetOpacity = 1 - Math.abs((halfWidth - trueDist)) / halfWidth;

            if (indicator == Indicator.CARDINAL) {
                context.br$fillRect(innerX, y, 1, 9, majorIndicatorColor.get().withAlpha((int) (majorIndicatorColor.get().getAlpha() * targetOpacity)));
                Color color = cardinalColor.get().withAlpha((int) (cardinalColor.get().getAlpha() * targetOpacity));
                if (color.getAlpha() > 4) {
                    context.br$drawCenteredString(getCardString(indicator, d), innerX + 1, y + 10, color, shadow.get());
                }
            } else if (indicator == Indicator.SEMI_CARDINAL) {
                Color color = semiCardinalColor.get().withAlpha((int) (semiCardinalColor.get().getAlpha() * targetOpacity));
                if (color.getAlpha() > 4) {
                    context.br$drawCenteredString(getCardString(indicator, d), innerX + 1, y + 1, color, shadow.get());
                }
            } else {
                context.br$fillRect(innerX, y, 1, 5, minorIndicatorColor.get().withAlpha((int) (minorIndicatorColor.get().getAlpha() * targetOpacity)));
            }
        }
        context.br$popMatrix();
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(widthOption);
        options.add(showDegrees);
        options.add(lookingBoxColor);
        options.add(degreesColor);
        options.add(cardinalColor);
        options.add(semiCardinalColor);
        options.add(majorIndicatorColor);
        options.add(minorIndicatorColor);
        return options;
    }

    @Override
    public AnchorPoint getAnchor() {
        return AnchorPoint.TOP_MIDDLE;
    }

    private enum Indicator {
        CARDINAL, SEMI_CARDINAL, SMALL
    }
}