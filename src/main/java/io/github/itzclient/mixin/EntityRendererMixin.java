package io.github.itzclient.mixin;

import io.github.itzclient.modules.hud.NametagHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @ModifyArgs(
        method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", 
        at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"
        )
    )
    private void itzclient$modifyNametag(Args args, T entity) {
        NametagHud nametagModule = NametagHud.getInstance();
        if (nametagModule.enabled.get() && nametagModule.showCustomTag.get()) {
            
            Text originalText = args.get(0);
            
            String tagString = nametagModule.customTagText.get() + " ";
            Text customTag = Text.literal(tagString).styled(style -> style.withColor(nametagModule.customTagColor.get().toInt()));
            
            Text newText;
            if (nametagModule.tagPosition.get() == NametagHud.TagPosition.LEFT) {
                newText = customTag.copy().append(originalText);
            } else {
                newText = originalText.copy().append(" ").append(customTag);
            }

            // Update the text being rendered
            args.set(0, newText);

            // Re-center the nametag by adjusting its x-position
            float oldWidth = MinecraftClient.getInstance().textRenderer.getWidth(originalText);
            float newWidth = MinecraftClient.getInstance().textRenderer.getWidth(newText);
            float oldX = args.get(1);
            args.set(1, oldX - (newWidth - oldWidth) / 2.0f);
        }
    }
}