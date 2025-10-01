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
package io.github.itzclient.modules.hud.gui.entry;

import io.github.itzclient.ItzClient;
import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.api.util.Color;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.modules.hud.util.Rectangle;
import io.github.itzclient.ui.widgets.DrawUtil;
import io.github.itzclient.util.ClientColors;

import java.util.List;

public abstract class BoxHudEntry extends AbstractHudEntry {

    protected BooleanOption background = new BooleanOption("background", true);
    protected ColorOption backgroundColor = new ColorOption("bgcolor", new Color(0x64000000));
    protected BooleanOption outline = new BooleanOption("outline", false);
    protected ColorOption outlineColor = new ColorOption("outlinecolor", ClientColors.WHITE);
    private final boolean backgroundAllowed;

    public BoxHudEntry(int width, int height, boolean backgroundAllowed) {
        super(width, height);
        this.backgroundAllowed = backgroundAllowed;
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        if (backgroundAllowed) {
            options.add(background);
            options.add(backgroundColor);
            options.add(outline);
            options.add(outlineColor);
        }
        return options;
    }

    @Override
    public void render(AxoRenderContext ctx, float delta) {
        ctx.br$pushMatrix();
        scale(ctx);
        if (backgroundAllowed && background.get()) {
            int opacity = ItzClient.config().hudBackgroundOpacity.get();
            Color finalBackgroundColor = backgroundColor.get().withAlpha(opacity);
            int cornerRadius = ItzClient.config().hudCornerRadius.get();
            if (finalBackgroundColor.getAlpha() > 0) {
                Rectangle bounds = getBounds();
                if (cornerRadius > 0) {
                    DrawUtil.drawRoundedRect(ctx, bounds.x(), bounds.y(), bounds.width(), bounds.height(), cornerRadius, finalBackgroundColor.toInt());
                } else {
                    ctx.br$fillRect(bounds, finalBackgroundColor);
                }
            }
            if (outline.get() && outlineColor.get().getAlpha() > 0) {
                ctx.br$outlineRect(getBounds(), outlineColor.get());
            }
        }
        renderComponent(ctx, delta);
        ctx.br$popMatrix();
    }
    
    public abstract void renderComponent(AxoRenderContext ctx, float delta);

    @Override
    public void renderPlaceholder(AxoRenderContext ctx, float delta) {
        ctx.br$pushMatrix();
        renderPlaceholderBackground(ctx);
        scale(ctx);
        renderPlaceholderComponent(ctx, delta);
        ctx.br$popMatrix();
        hovered = false;
    }

    public abstract void renderPlaceholderComponent(AxoRenderContext ctx, float delta);
}