package io.github.itzclient.ui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.Text;

public class StyledButtonWidget extends ButtonWidget {

    private static final int NORMAL_COLOR = 0x80333333; // Semi-transparent dark grey
    private static final int HOVER_COLOR = 0x80555555;  // Slightly lighter grey on hover
    private static final int TEXT_COLOR = 0xFFFFFFFF;     // White text

    public StyledButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int color = this.isHovered() ? HOVER_COLOR : NORMAL_COLOR;

        // Use our DrawUtil to create a rounded background for the button
        DrawUtil.drawRoundedRect(graphics, this.getX(), this.getY(), this.width, this.height, 5, color);

        // Draw the button's text centered
        graphics.drawCenteredShadowedText(
            MinecraftClient.getInstance().textRenderer,
            this.getMessage(),
            this.getX() + this.width / 2,
            this.getY() + (this.height - 8) / 2,
            TEXT_COLOR
        );
    }
}