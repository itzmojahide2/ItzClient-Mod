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
package io.github.itzclient.util;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClientColors {

    // --- Core Brand Colors ---
    // These colors define the look of ItzClient.
    // If you want to change the theme, you only need to change these values.
    public static Color WHITE = Colors.WHITE;
    public static Color BLACK = Colors.BLACK;
    public static Color GRAY = Colors.GRAY;
    public static Color DARK_GRAY = Colors.DARK_GRAY;
    public static Color GOLD = Color.parse("#b8860b").immutable();

    // --- UI Interaction Colors ---
    public static Color SELECTOR_RED = new Color(220, 40, 40).immutable(); // For health bars, important highlights
    public static Color SELECTOR_GREEN = new Color(53, 219, 103).immutable(); // For "enabled" states
    public static Color SELECTOR_BLUE = new Color(51, 153, 255, 255).immutable(); // For labels and accents

    /**
     * Blends two Colors based off of a percentage.
     * @param original   The starting color.
     * @param blend      The target color.
     * @param percentage The blend percentage (0.0f to 1.0f).
     * @return The blended color.
     */
    public static Color blend(Color original, Color blend, float percentage) {
        if (percentage >= 1.0f) return blend;
        if (percentage <= 0.0f) return original;
        
        int red = blendInt(original.getRed(), blend.getRed(), percentage);
        int green = blendInt(original.getGreen(), blend.getGreen(), percentage);
        int blue = blendInt(original.getBlue(), blend.getBlue(), percentage);
        int alpha = blendInt(original.getAlpha(), blend.getAlpha(), percentage);
        
        return new Color(red, green, blue, alpha);
    }

    private static int blendInt(int start, int end, float percent) {
        int diff = end - start;
        return start + Math.round(diff * percent);
    }

    // A simple utility class for ARGB color manipulation, often used in rendering.
    public static class ARGB {
        public static int opaque(int color) {
            return color | 0xFF000000;
        }
    }
              }
