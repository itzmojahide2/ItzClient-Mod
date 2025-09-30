package io.github.itzclient.ui.widgets;

import net.minecraft.client.gui.GuiGraphics;

public class DrawUtil {

    /**
     * Draws a rectangle with filled, rounded corners.
     * @param graphics The GuiGraphics instance for drawing.
     * @param x The x-coordinate of the top-left corner.
     * @param y The y-coordinate of the top-left corner.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param radius The radius of the corners.
     * @param color The color of the rectangle in ARGB format.
     */
    public static void drawRoundedRect(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
        // Draw the central rectangular parts
        graphics.fill(x + radius, y, x + width - radius, y + height);
        graphics.fill(x, y + radius, x + width, y + height - radius);
        
        // Draw the four rounded corner parts
        drawCirclePart(graphics, x + radius, y + radius, radius, 1, color); // Top-left
        drawCirclePart(graphics, x + width - radius, y + radius, radius, 2, color); // Top-right
        drawCirclePart(graphics, x + radius, y + height - radius, radius, 4, color); // Bottom-left
        drawCirclePart(graphics, x + width - radius, y + height - radius, radius, 3, color); // Bottom-right
    }

    /**
     * Helper method to draw a quarter-circle for the rounded corners.
     */
    private static void drawCirclePart(GuiGraphics graphics, int centerX, int centerY, int radius, int part, int color) {
        for (int i = 0; i <= 90; i++) {
            double angle = Math.toRadians(i);
            double sin = Math.sin(angle) * radius;
            double cos = Math.cos(angle) * radius;

            if (part == 1) { // Top-left corner
                graphics.fill(centerX - (int)cos, centerY - (int)sin, centerX, centerY, color);
            } else if (part == 2) { // Top-right corner
                graphics.fill(centerX, centerY - (int)sin, centerX + (int)cos, centerY, color);
            } else if (part == 3) { // Bottom-right corner
                graphics.fill(centerX, centerY, centerX + (int)cos, centerY + (int)sin, color);
            } else if (part == 4) { // Bottom-left corner
                graphics.fill(centerX - (int)cos, centerY, centerX, centerY + (int)sin, color);
            }
        }
    }
            }
