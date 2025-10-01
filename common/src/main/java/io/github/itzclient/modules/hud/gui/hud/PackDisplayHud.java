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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.texture.NativeImage;
import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.TextHudEntry;
import io.github.itzclient.modules.hud.util.DrawPosition;
import lombok.Getter;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.ResourceIoSupplier;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PackDisplayHud extends TextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "packdisplayhud");

    // --- State and Settings ---
    private final List<PackWidget> widgets = new ArrayList<>();
    private final BooleanOption iconsOnly = new BooleanOption("iconsonly", false);
    private PackWidget placeholder;

    public PackDisplayHud() {
        super(200, 50, true);
    }

    @Override
    public void init() {
        // Initial population of the pack list
        update();
    }
    
    @Override
    public void renderComponent(AxoRenderContext context, float f) {
        DrawPosition pos = getPos();

        if (widgets.isEmpty() && client.br$getResourcePackManager().getEnabledProfiles().size() > 1) {
            update(); // Try to re-initialize if the list is empty but should have packs
        }

        int y = pos.y() + 1;
        // Render packs from top to bottom (reverse of how they are layered in-game)
        for (int i = widgets.size() - 1; i >= 0; i--) {
            widgets.get(i).render(context, pos.x() + 1, y);
            y += 18;
        }

        // Dynamically adjust height
        int requiredHeight = Math.max(18, widgets.size() * 18);
        if (getHeight() != requiredHeight) {
            setHeight(requiredHeight);
            onBoundsUpdate();
        }
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext graphics, float delta) {
        if (placeholder == null) {
            try (ResourcePack defaultPack = client.br$getDefaultResourcePack()) {
                placeholder = createWidget(Text.of("Default"), defaultPack);
            } catch (Exception ignored) {}
        }
        
        if (placeholder != null) {
            placeholder.render(graphics, getPos().x() + 1, getPos().y() + 1);
        }
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(iconsOnly);
        return options;
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    public void update() {
        widgets.clear();
        client.br$getResourcePackManager().getEnabledProfiles().forEach(profile -> {
            try (ResourcePack pack = profile.createPack()) {
                if (client.br$getResourcePackManager().getProfiles().size() == 1 || !pack.getName().equalsIgnoreCase("vanilla")) {
                    PackWidget widget = createWidget(profile.getDisplayName(), pack);
                    if (widget != null) {
                        widgets.add(widget);
                    }
                }
            } catch (Exception ignored) {}
        });

        // Dynamically adjust width
        AtomicInteger w = new AtomicInteger(20);
        widgets.forEach(packWidget -> {
            int textW = client.br$getFont().br$getWidth(packWidget.getName()) + 20;
            if (textW > w.get()) {
                w.set(textW);
            }
        });
        setWidth(w.get());
        onBoundsUpdate();
    }

    private PackWidget createWidget(Text displayName, ResourcePack pack) throws IOException {
        ResourceIoSupplier<InputStream> supplier = pack.openRoot("pack.png");
        if (supplier == null) return null;

        try (InputStream stream = supplier.get()) {
            if (stream != null) {
                Identifier id = client.br$getTextureManager().registerDynamicTexture(ID.br$getPath() + "/" + pack.getName(), new NativeImageBackedTexture(NativeImage.read(stream)));
                return new PackWidget(displayName, id);
            }
        }
        return null;
    }

    class PackWidget {
        @Getter
        private final String name;
        private final Identifier texture;

        public PackWidget(Text name, Identifier id) {
            this.name = name.getString();
            this.texture = id;
        }

        public void render(AxoRenderContext graphics, int x, int y) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.br$drawTexture(x, y, 16, 16, texture, 0, 0, 16, 16, 16, 16);
            
            if (!iconsOnly.get()) {
                graphics.br$drawString(name, x + 18, y + 4, textColor.get().toInt(), shadow.get());
            }
        }
    }
}