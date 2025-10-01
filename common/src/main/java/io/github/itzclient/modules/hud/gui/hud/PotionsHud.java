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
import io.github.itzclient.bridge.entity.effect.AxoStatusEffectInstance;
import io.github.itzclient.bridge.entity.effect.AxoStatusEffects;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.bridge.util.AxoText;
import io.github.itzclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.itzclient.modules.hud.gui.entry.TextHudEntry;
import io.github.itzclient.modules.hud.gui.layout.AnchorPoint;
import io.github.itzclient.modules.hud.gui.layout.CardinalOrder;
import io.github.itzclient.modules.hud.util.DefaultOptions;
import io.github.itzclient.modules.hud.util.Rectangle;
import io.github.itzclient.util.CommonUtil;

import java.util.List;

public class PotionsHud extends TextHudEntry implements DynamicallyPositionable {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "potionshud");

    // --- Settings for this module ---
    private final EnumOption<AnchorPoint> anchor = DefaultOptions.getAnchorPoint();
    private final EnumOption<CardinalOrder> order = DefaultOptions.getCardinalOrder(CardinalOrder.TOP_DOWN);
    private final BooleanOption iconsOnly = new BooleanOption("iconsonly", false);
    private final BooleanOption showEffectName = new BooleanOption("showEffectNames", true);
    private final ColorOption timerTextColor = new ColorOption("potionshud.timer_text_color", Color.parse("#7F7F7F"));

    public PotionsHud() {
        super(50, 200, true);
        background = new BooleanOption("background", false);
    }

    private static AxoText formatNameAndAmplifier(AxoStatusEffectInstance effect) {
        return effect.br$getType().br$getDisplayName().br$copy()
            .br$append(" ")
            .br$append(CommonUtil.toRoman(effect.br$getAmplifier() + 1));
    }

    @Override
    public void renderComponent(AxoRenderContext context, float delta) {
        if (client.br$getPlayer() == null) {
            return;
        }
        renderEffects(context, client.br$getPlayer().br$getStatusEffects());
    }

    private void renderEffects(AxoRenderContext graphics, List<AxoStatusEffectInstance> effects) {
        boolean noEffects = effects.isEmpty();
        
        // Dynamically calculate the required size of the HUD
        int calcWidth = noEffects ? 50 : calculateWidth(effects);
        int calcHeight = noEffects ? 20 : calculateHeight(effects);
        
        if (calcWidth != getWidth() || calcHeight != getHeight()) {
            setWidth(calcWidth);
            setHeight(calcHeight);
            onBoundsUpdate();
        }
        
        if (noEffects) {
            return;
        }

        int lastPos = 0;
        CardinalOrder direction = order.get();
        Rectangle bounds = getBounds();
        int x = bounds.x();
        int y = bounds.y();

        for (int i = 0; i < effects.size(); i++) {
            AxoStatusEffectInstance effect = effects.get(direction.getDirection() == -1 ? i : effects.size() - i - 1);
            
            if (direction.isXAxis()) { // Horizontal layout
                renderPotion(graphics, effect, x + lastPos + 1, y + 1);
                int nameWidth = 0;
                if (!iconsOnly.get()) {
                    nameWidth += graphics.br$getFont().br$getWidth(effect.br$formatDuration()) + 1;
                    if (showEffectName.get()) {
                        nameWidth = Math.max(nameWidth, client.br$getFont().br$getWidth(formatNameAndAmplifier(effect)));
                    }
                }
                lastPos += 20 + nameWidth;
            } else { // Vertical layout
                renderPotion(graphics, effect, x + 1, y + 1 + lastPos);
                lastPos += 20;
            }
        }
    }

    private int calculateWidth(List<AxoStatusEffectInstance> effects) {
        if (order.get().isXAxis()) { // Horizontal
            if (iconsOnly.get()) return 20 * effects.size() + 2;
            int totalWidth = effects.stream().mapToInt(effect -> {
                int iconWidth = 20;
                int textWidth = 0;
                if (!iconsOnly.get()) {
                    textWidth = client.br$getFont().br$getWidth(effect.br$formatDuration());
                    if (showEffectName.get()) {
                        textWidth = Math.max(textWidth, client.br$getFont().br$getWidth(formatNameAndAmplifier(effect)));
                    }
                }
                return iconWidth + textWidth;
            }).sum();
            return totalWidth + 2;
        } else { // Vertical
            if (iconsOnly.get()) return 20;
            int maxTextWidth = effects.stream().mapToInt(effect -> {
                if (showEffectName.get()) {
                    return client.br$getFont().br$getWidth(formatNameAndAmplifier(effect));
                }
                return client.br$getFont().br$getWidth(effect.br$formatDuration());
            }).max().orElse(0);
            return 22 + maxTextWidth;
        }
    }

    private int calculateHeight(List<AxoStatusEffectInstance> effects) {
        return order.get().isXAxis() ? 20 : 20 * effects.size() + 2;
    }

    private void renderPotion(AxoRenderContext graphics, AxoStatusEffectInstance effect, int x, int y) {
        graphics.br$drawTexture(x, y, 18, 18, effect.br$getType().br$getSprite());
        if (!iconsOnly.get()) {
            if (showEffectName.get()) {
                graphics.br$drawString(formatNameAndAmplifier(effect), x + 20, y + 1, textColor.get().toInt(), shadow.get());
                graphics.br$drawString(effect.br$formatDuration(), x + 20, y + 11, timerTextColor.get().toInt(), shadow.get());
            } else {
                graphics.br$drawString(effect.br$formatDuration(), x + 20, y + 5, timerTextColor.get().toInt(), shadow.get());
            }
        }
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
        List<AxoStatusEffectInstance> placeholderEffects = List.of(
            AxoStatusEffectInstance.create(AxoStatusEffects.SPEED, 9999),
            AxoStatusEffectInstance.create(AxoStatusEffects.JUMP_BOOST, 99999),
            AxoStatusEffectInstance.create(AxoStatusEffects.HASTE, -1)
        );
        renderEffects(context, placeholderEffects);
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(anchor);
        options.add(order);
        options.add(iconsOnly);
        options.add(showEffectName);
        options.add(timerTextColor);
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