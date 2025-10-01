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
import io.github.itzclient.AxolotlClientConfig.api.util.Color;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoI18n;
import io.github.itzclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.itzclient.modules.hud.gui.layout.AnchorPoint;
import io.github.itzclient.modules.hud.gui.layout.Justification;
import io.github.itzclient.modules.hud.util.DefaultOptions;
import io.github.itzclient.modules.hud.util.DrawPosition;
import java.util.List;

public abstract class SimpleTextHudEntry extends TextHudEntry implements DynamicallyPositionable {

    protected final EnumOption<Justification> justification = new EnumOption<>("justification", Justification.class, Justification.CENTER);
    protected final EnumOption<AnchorPoint> anchor = DefaultOptions.getAnchorPoint();
    protected final BooleanOption showBrackets = new BooleanOption("show_brackets", false);
    private final IntegerOption minWidth, minHeight;

    public SimpleTextHudEntry() { this(53, 13, true); }
    public SimpleTextHudEntry(int width) { this(width, 13, true); }
    public SimpleTextHudEntry(int width, int height, boolean backgroundAllowed) {
        super(width, height, backgroundAllowed);
        minWidth = new IntegerOption("minwidth", width, 1, 300);
        minHeight = new IntegerOption("hud.height", height, 1, 150);
    }

    @Override
    public void renderComponent(AxoRenderContext render, float delta) {
        render.br$glEnableBlend();
        DrawPosition pos = getPos();
        String value = wrapWithBrackets(getValue());
        int valueWidth = render.br$getFont().br$getWidth(value);
        int elementWidth = valueWidth + 4;
        int elementHeight = client.br$getFont().br$getFontHeight() + 4;
        boolean boundsChanged = false;
        int minW = minWidth.get();
        if (elementWidth < minW) { if (width != minW) { setWidth(minW); boundsChanged = true; }
        } else if (elementWidth != width) { setWidth(elementWidth); boundsChanged = true; }
        int minH = minHeight.get();
        if (elementHeight < minH) { if (height != minH) { setHeight(minH); boundsChanged = true; }
        } else if (elementHeight != height) { setHeight(elementHeight); boundsChanged = true; }
        if (boundsChanged) { onBoundsUpdate(); }
        render.br$drawString(value, pos.x() + justification.get().getXOffset(valueWidth, getWidth() - 4) + 2, pos.y() + (Math.round((float) getHeight() / 2)) - 4, getTextColor().toInt(), shadow.get());
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext ctx, float delta) {
        DrawPosition pos = getPos();
        String value = wrapWithBrackets(getPlaceholder());
        ctx.br$drawString(value, pos.x() + justification.get().getXOffset(value, getWidth() - 4) + 2, pos.y() + (Math.round((float) getHeight() / 2)) - 4, getTextColor().toInt(), shadow.get());
    }

    protected String wrapWithBrackets(String value) {
        return showBrackets.get() ? AxoI18n.translate("bracket_format", value) : value;
    }

    public abstract String getPlaceholder();
    public abstract String getValue();
    public Color getTextColor() { return textColor.get(); }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(justification);
        options.add(anchor);
        options.add(minWidth);
        options.add(minHeight);
        options.add(showBrackets);
        return options;
    }

    @Override
    public AnchorPoint getAnchor() { return anchor.get(); }
}