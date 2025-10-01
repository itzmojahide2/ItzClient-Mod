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
package io.github.itzclient.modules.hud;

import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.modules.hud.gui.hud.KeystrokesHud;
import io.github.itzclient.modules.hud.gui.hud.PackDisplayHud;
import io.github.itzclient.modules.hud.gui.hud.PlayerHud;
import io.github.itzclient.modules.hud.gui.hud.vanilla.*;
import io.github.itzclient.ui.screens.HudEditorScreen; // CORRECTED IMPORT for our new UI
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

public class HudManager extends HudManagerCommon {
    @Getter
    private final static HudManager instance = new HudManager();

    @Override
    protected void openScreen() {
        // This now opens our new, clean HUD editor screen.
        MinecraftClient.getInstance().setScreen(new HudEditorScreen());
    }

    @Override
    protected void addExtraHud() {
        // These are the HUDs specific to this version of Minecraft
        add(new ActionBarHud());
        add(new BossBarHud());
        add(new CrosshairHud());
        add(new DebugCountersHud());
        add(new HotbarHUD());
        add(new ScoreboardHud());
        // KeystrokesHud is already in ItzClient.java, but this would be a place for it too
        add(new PackDisplayHud());
        add(new PlayerHud());
    }

    @Override
    public void render(AxoRenderContext context, float delta) {
        final var mc = MinecraftClient.getInstance();
        mc.getProfiler().push("Hud render");
        if(!(mc.currentScreen instanceof HudEditorScreen)) {
            super.render(context, delta);
        }
        mc.getProfiler().pop();
    }
}