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