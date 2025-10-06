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
package io.github.itzclient.mixin;

import io.github.itzclient.ui.screens.HudEditorScreen;
import net.minecraft.client.MinecraftClient; // Import MinecraftClient
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow; // Import Shadow
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    // --- START OF FIX ---
    // These @Shadow annotations link the fields to the ones in the parent Screen class.
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    protected MinecraftClient client;
    // --- END OF FIX ---

    private static final Identifier MOD_MENU_ICON = Identifier.of("minecraft", "textures/gui/sprites/widget/server_list.png");

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("RETURN"))
    private void addModMenuButton(CallbackInfo ci) {
        // This code will now compile correctly because 'width', 'height', and 'client' are accessible.
        this.addDrawableChild(new TexturedButtonWidget(
            this.width / 2 + 104,
            this.height / 4 + 72 + -16,
            20, 20,
            MOD_MENU_ICON,
            (button) -> this.client.setScreen(new HudEditorScreen())
        ));
    }
}