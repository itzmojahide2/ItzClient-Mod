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

import io.github.itzclient.ui.screens.ItzMainMenuScreen;
import net.minecraft.client.MinecraftClient; // Import MinecraftClient
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow; // Import Shadow
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    // --- START OF FIX ---
    // Add this @Shadow annotation to guarantee access to the client field.
    @Shadow
    protected MinecraftClient client;
    // --- END OF FIX ---

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void itzclient$replaceMainMenu(CallbackInfo ci) {
        ci.cancel();
        // This will now compile correctly.
        this.client.setScreen(new ItzMainMenuScreen());
    }
}