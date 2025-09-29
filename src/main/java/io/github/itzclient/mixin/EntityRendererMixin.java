package io.github.itzclient.mixin;

import io.github.itzclient.modules.hud.NametagHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
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
        locals = LocalCapture.CAPTURE-FAILEXCEPTION
    )
    private void itzclient$renderCustomNametag(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci,
                                               double d, int y, Matrix4f matrix4f) {
        
        NametagHud nametagModule = NametagHud.getInstance();
        if (nametagModule.enabled.get()) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            
            MutableText fullText = Text.empty();
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
                graphics.drawTexture(ITZ_TAG_ICON, (int)iconX, -1, 0, 0, iconSize, iconSize, iconSize, iconSize);
            }
            
            textRenderer.draw(nameAndTag, textX, 0.0f, 0x20FFFFFF, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, 0, light);
            textRenderer.draw(nameAndTag, textX, 0.0f, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        
            ci.cancel();
        }
    }
}