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

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.itzclient.modules.hud.NametagHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Unique
    private static final Identifier ITZ_TAG_ICON = Identifier.of("itzclient", "textures/gui/tag_icon.png");

    @Inject(
        method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void itzclient$renderCustomNametag(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci,
                                               double d, int y, Matrix4f matrix4f) {
        
        NametagHud nametagModule = NametagHud.getInstance();
        if (nametagModule.enabled.get()) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            
            String tagString = nametagModule.customTagText.get();
            Text customTag = Text.literal(tagString).styled(style -> style.withColor(nametagModule.customTagColor.get().toInt()));
            
            int iconSize = 10;
            int iconPadding = 2;
            int totalIconWidth = iconSize + iconPadding;

            Text nameAndTag;
            if (nametagModule.showCustomTag.get()) {
                if (nametagModule.tagPosition.get() == NametagHud.TagPosition.LEFT) {
                    nameAndTag = customTag.copy().append(" ").append(text);
                } else {
                    nameAndTag = text.copy().append(" ").append(customTag);
                }
            } else {
                nameAndTag = text.copy();
            }

            float textWidth = textRenderer.getWidth(nameAndTag);
            float totalWidth = textWidth + (nametagModule.showCustomTag.get() ? totalIconWidth : 0);
            float xOffset = -totalWidth / 2.0f;
            
            float iconX = xOffset;
            if (nametagModule.tagPosition.get() == NametagHud.TagPosition.RIGHT) {
                iconX = xOffset + textWidth + iconPadding;
            }
            
            float textX = xOffset;
            if (nametagModule.showCustomTag.get() && nametagModule.tagPosition.get() == NametagHud.TagPosition.LEFT) {
                textX += totalIconWidth;
            }

            if (nametagModule.showCustomTag.get()) {
                GuiGraphics graphics = new GuiGraphics(MinecraftClient.getInstance(), vertexConsumers.getBuffer(RenderLayer.getText()));
                graphics.setMatrices(matrices);

                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, ITZ_TAG_ICON);
                RenderSystem.texParameteri(3553, 10241, 9728); // GL_NEAREST for sharp scaling
                RenderSystem.texParameteri(3553, 10240, 9728);
                
                graphics.drawTexture(ITZ_TAG_ICON, (int)iconX, -1, 0, 0, iconSize, iconSize, iconSize, iconSize);

                RenderSystem.texParameteri(3553, 10241, 9729); // Restore default GL_LINEAR
                RenderSystem.texParameteri(3553, 10240, 9729);
            }
            
            textRenderer.draw(nameAndTag, textX, 0.0f, 0x20FFFFFF, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, 0, light);
            textRenderer.draw(nameAndTag, textX, 0.0f, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        
            ci.cancel();
        }
    }
    }
