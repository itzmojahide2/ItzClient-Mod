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
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.itzclient.bridge.PlatformDispatch;
import io.github.itzclient.bridge.events.Events;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.render.AxoSprite;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.itzclient.modules.hud.gui.entry.TextHudEntry;
import io.github.itzclient.modules.hud.gui.layout.AnchorPoint;
import io.github.itzclient.modules.hud.util.DrawPosition;

import java.util.List;

public class IPHud extends TextHudEntry implements DynamicallyPositionable {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "iphud");

    private final BooleanOption showIcon = new BooleanOption("iphud.show_icon", false);
    private final IntegerOption heightOption = new IntegerOption("hud.height", 13, 9, 64);
    private final EnumOption<AnchorPoint> anchor = new EnumOption<>("anchorpoint", AnchorPoint.class, AnchorPoint.TOP_LEFT);
    private AxoSprite.Dynamic sprite;
    private String serverAddress = "Singleplayer";
    private int tickCounter = 0;

    public IPHud() {
        super(115, 13, true);
        Events.DISCONNECT.register(() -> {
            if (sprite != null) {
                sprite.close();
                sprite = null;
            }
        });
        Events.CONNECTION_PLAY_READY.register(() -> {
            tickCounter = 20;
        });
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        var options = super.getConfigurationOptions();
        options.add(showIcon);
        options.add(heightOption);
        options.add(anchor);
        return options;
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            if (client.br$isLocalServer() || client.br$getServerAddress() == null) {
                serverAddress = "Singleplayer";
                if (sprite != null) {
                    sprite.close();
                    sprite = null;
                }
            } else {
                serverAddress = client.br$getServerAddress();
                if (showIcon.get() && sprite == null) {
                    sprite = PlatformDispatch.ipHud$getServerIcon();
                }
            }
            if (!showIcon.get() && sprite != null) {
                sprite.close();
                sprite = null;
            }
        }
    }

    private void updateSize(AxoRenderContext graphics) {
        int hNew = heightOption.get();
        if (getHeight() != hNew) {
            setHeight(hNew);
        }
        int requiredWidth = graphics.br$getFont().br$getWidth(serverAddress) + 4;
        if (showIcon.get() && sprite != null) {
            requiredWidth += getHeight() + 1;
        }
        if (getWidth() != requiredWidth) {
            setWidth(requiredWidth);
            onBoundsUpdate();
        }
    }

    @Override
    public void renderComponent(AxoRenderContext graphics, float delta) {
        updateSize(graphics);
        DrawPosition pos = getPos();
        int textX = pos.x() + getWidth() / 2;
        if (showIcon.get() && sprite != null) {
            int imageSize = getHeight() - 2;
            textX += imageSize / 2;
            graphics.br$drawTexture(pos.x() + 1, pos.y() + 1, imageSize, imageSize, sprite);
        }
        graphics.br$drawCenteredString(serverAddress, textX, pos.y() + getHeight() / 2 - client.br$getFont().br$getFontHeight() / 2, textColor.get().toInt(), true);
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
        updateSize(context);
        DrawPosition pos = getPos();
        int textX = pos.x() + getWidth() / 2;
        if (showIcon.get()) {
            int imageSize = getHeight() - 2;
            textX += imageSize / 2;
            context.br$fillRect(pos.x() + 1, pos.y() + 1, imageSize, imageSize, 0x80FFFFFF);
        }
        context.br$drawCenteredString(serverAddress, textX, pos.y() + getHeight() / 2 - client.br$getFont().br$getFontHeight() / 2, textColor.get().toInt(), shadow.get());
    }

    @Override
    public AnchorPoint getAnchor() {
        return anchor.get();
    }
}