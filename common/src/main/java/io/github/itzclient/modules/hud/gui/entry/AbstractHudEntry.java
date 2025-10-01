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

import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.itzclient.AxolotlClientConfig.impl.options.DoubleOption;
import io.github.itzclient.bridge.AxoMinecraftClient;
import io.github.itzclient.bridge.render.AxoWindow;
import io.github.itzclient.modules.hud.gui.component.HudEntry;
import io.github.itzclient.modules.hud.util.DefaultOptions;
import io.github.itzclient.modules.hud.util.DrawPosition;
import io.github.itzclient.modules.hud.util.Rectangle;
import io.github.itzclient.util.ClientColors;
import io.github.itzclient.util.MathUtil;
import io.github.itzclient.util.options.ForceableBooleanOption;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHudEntry implements HudEntry {
    @Getter
    protected final ForceableBooleanOption enabled = DefaultOptions.getEnabled();
    protected final DoubleOption scale = DefaultOptions.getScale(this);
    protected final AxoMinecraftClient client = AxoMinecraftClient.getInstance();
    private final DoubleOption x = DefaultOptions.getX(getDefaultX(), this);
    private final DoubleOption y = DefaultOptions.getY(getDefaultY(), this);
    @Setter
    @Getter
    protected int width;
    @Setter
    @Getter
    protected int height;
    @Setter
    @Getter
    protected boolean hovered = false;
    @Getter
    private Rectangle trueBounds;
    private Rectangle renderBounds;
    private DrawPosition truePosition;
    private DrawPosition renderPosition;
    private OptionCategory category;

    public AbstractHudEntry(int width, int height) {
        this.width = width;
        this.height = height;
        truePosition = new DrawPosition(0, 0);
        renderPosition = new DrawPosition(0, 0);
        renderBounds = new Rectangle(0, 0, 1, 1);
        trueBounds = new Rectangle(0, 0, 1, 1);
    }

    public static float intToFloat(int current, int max, int offset) {
        return MathUtil.clamp((float) (current) / (max - offset), 0, 1);
    }

    public static int floatToInt(float percent, int max, int offset) {
        return MathUtil.clamp(Math.round((max - offset) * percent), 0, max);
    }

    public void renderPlaceholderBackground(AxoRenderContext context) {
        if (hovered) {
            context.br$fillRect(getTrueBounds(), ClientColors.SELECTOR_BLUE.withAlpha(100));
        } else {
            context.br$fillRect(getTrueBounds(), ClientColors.WHITE.withAlpha(50));
        }
        context.br$outlineRect(getTrueBounds(), ClientColors.BLACK);
    }

    public void scale(AxoRenderContext context) {
        float scale = getScale();
        context.br$scaleMatrix(scale, scale, 1);
    }

    @Override
    public int getRawTrueX() { return truePosition.x(); }
    @Override
    public void setX(int x) { this.x.set((double) intToFloat(x, (int) AxoWindow.getWindow().br$getScaledWidth(), 0)); }
    @Override
    public float getScale() { return scale.get().floatValue(); }
    @Override
    public void setScale(float scale) { this.scale.set((double) scale); }
    @Override
    public int getRawX() { return getPos().x; }
    @Override
    public int getRawTrueY() { return truePosition.y(); }
    @Override
    public int getRawY() { return getPos().y(); }
    @Override
    public void setY(int y) { this.y.set((double) intToFloat(y, (int) AxoWindow.getWindow().br$getScaledHeight(), 0)); }
    
    public Rectangle getBounds() { return renderBounds; }
    @Override
    public DrawPosition getPos() { return renderPosition; }
    @Override
    public DrawPosition getTruePos() { return truePosition; }
    @Override
    public int getTrueWidth() { return trueBounds == null ? HudEntry.super.getTrueWidth() : trueBounds.width(); }
    @Override
    public int getTrueHeight() { return trueBounds == null ? HudEntry.super.getTrueHeight() : trueBounds.height(); }
    
    @Override
    public void onBoundsUpdate() {
        setBounds(getScale());
    }
    
    public void setBounds(float scale) {
        final var window = AxoWindow.getWindow();
        if (window == null) {
            truePosition = new DrawPosition(0, 0); renderPosition = new DrawPosition(0, 0);
            renderBounds = new Rectangle(0, 0, 1, 1); trueBounds = new Rectangle(0, 0, 1, 1);
            return;
        }
        int scaledX = floatToInt(x.get().floatValue(), (int) window.br$getScaledWidth(), 0) - offsetTrueWidth();
        int scaledY = floatToInt(y.get().floatValue(), (int) window.br$getScaledHeight(), 0) - offsetTrueHeight();
        if (scaledX < 0) scaledX = 0;
        if (scaledY < 0) scaledY = 0;
        int trueWidth = (int) (getWidth() * getScale());
        if (trueWidth < window.br$getScaledWidth() && scaledX + trueWidth > window.br$getScaledWidth()) {
            scaledX = (int) (window.br$getScaledWidth() - trueWidth);
        }
        int trueHeight = (int) (getHeight() * getScale());
        if (trueHeight < window.br$getScaledHeight() && scaledY + trueHeight > window.br$getScaledHeight()) {
            scaledY = (int) (window.br$getScaledHeight() - trueHeight);
        }
        truePosition.x = scaledX; truePosition.y = scaledY;
        renderPosition = truePosition.divide(getScale());
        renderBounds = new Rectangle(renderPosition.x(), renderPosition.y(), getWidth(), getHeight());
        trueBounds = new Rectangle(scaledX, scaledY, (int) (getWidth() * getScale()), (int) (getHeight() * getScale()));
    }

    public OptionCategory getAllOptions() {
        if (category == null) {
            category = OptionCategory.create(getNameKey());
            getSaveOptions().forEach(category::add);
        }
        return category;
    }

    @Override
    public List<Option<?>> getSaveOptions() {
        List<Option<?>> options = getConfigurationOptions();
        options.add(x);
        options.add(y);
        return options;
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = new ArrayList<>();
        options.add(enabled);
        options.add(scale);
        return options;
    }

    @Override
    public OptionCategory getCategory() { return category; }
    @Override
    public boolean isEnabled() { return enabled.get(); }
    @Override
    public void setEnabled(boolean value) { enabled.set(value); }
}