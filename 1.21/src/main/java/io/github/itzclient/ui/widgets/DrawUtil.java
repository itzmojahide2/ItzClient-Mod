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
