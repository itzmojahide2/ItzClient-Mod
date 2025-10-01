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

import io.github.itzclient.ItzClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public abstract class LightmapManagerMixin {

    /**
     * Redirects the call to get the gamma (brightness) setting.
     * If Fullbright is enabled in the ItzClient config, this method returns a
     * fake Option with a very high gamma value, forcing the game to render
     * everything at maximum brightness. Otherwise, it returns the player's
     * actual gamma setting.
     */
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getGamma()Lnet/minecraft/client/option/Option;"))
    public Option<Double> itzclient$fullbright(GameOptions instance) {
        // Check if our Fullbright option is enabled
        if (ItzClient.config().fullBright.get()) {
            // Return a temporary, fake setting with a high gamma value
            return new Option<>(
                "options.gamma",
                Option.emptyTooltip(),
                (optionText, value) -> optionText,
                Option.UnitDoubleValueSet.INSTANCE,
                15.0D, // High value for max brightness
                value -> {}
            );
        }
        
        // If Fullbright is off, proceed with the game's normal behavior
        return instance.getGamma();
    }
}