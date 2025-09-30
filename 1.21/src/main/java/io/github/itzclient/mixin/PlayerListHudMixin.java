package io.github.itzclient.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.itzclient.util.ItzUserManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Unique
    private static final Identifier ITZ_ICON = Identifier.of("itzclient", "textures/gui/tag_icon.png");

    /**
     * This mixin wraps the function that draws the player's name in the tab list.
     * Before the name is drawn, we check if the player is an ItzClient user.
     * If they are, we draw our icon to the left of their name.
     */
    @WrapOperation(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawShadowedText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I")
    )
    private int itzclient$drawIconInTab(GuiGraphics instance, TextRenderer textRenderer, Text text, int x, int y, int color, Operation<Integer> original, @Local PlayerListEntry entry) {
        
        if (ItzUserManager.isItzUser(entry.getProfile().getId())) {
            int iconSize = 8; // A smaller icon for the tab list
            // Draw our icon to the left of where the name will be drawn.
            instance.drawTexture(ITZ_ICON, x - iconSize - 2, y + 1, 0, 0, iconSize, iconSize, iconSize, iconSize);
        }

        // Call the original function to draw the player's name.
        return original.call(instance, textRenderer, text, x, y, color);
    }
}
