package io.github.itzclient.mixin;

import io.github.itzclient.ItzClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    // Use the main client icon for the watermark
    @Unique
    private static final Identifier ITZ_WATERMARK_ICON = Identifier.of("itzclient", "icon.png");
    
    @Unique
    private static final Text ITZ_WATERMARK_TEXT = Text.literal("ItzClient");

    /**
     * Injects into the end of every screen's render method to draw our watermark on top.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void itzclient$renderBrandingWatermark(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Ensure the client and player are loaded
        if (this.client == null || this.client.player == null) {
            return;
        }

        // Check if the user has this feature enabled in the config
        if (ItzClient.config().showBrandingWatermark.get()) {
            int screenWidth = this.width;
            int screenHeight = this.height;

            int iconSize = 12;
            int padding = 5;
            
            // Calculate the width of the text "ItzClient"
            int textWidth = this.client.textRenderer.getWidth(ITZ_WATERMARK_TEXT);
            
            // Calculate the total width of the icon + padding + text
            int totalWidth = iconSize + padding + textWidth;

            // Position the watermark in the bottom-right corner
            int x = screenWidth - totalWidth - padding;
            int y = screenHeight - iconSize - padding;

            // Draw the icon
            graphics.drawTexture(ITZ_WATERMARK_ICON, x, y, 0, 0, iconSize, iconSize, iconSize, iconSize);

            // Draw the text next to the icon, vertically centered
            // The color 0x90FFFFFF is white with about 56% transparency, making it subtle.
            graphics.drawText(
                this.client.textRenderer,
                ITZ_WATERMARK_TEXT,
                x + iconSize + padding,
                y + (iconSize / 2) - (this.client.textRenderer.fontHeight / 2),
                0x90FFFFFF, // White with transparency
                true // with shadow
            );
        }
    }
                                 }
