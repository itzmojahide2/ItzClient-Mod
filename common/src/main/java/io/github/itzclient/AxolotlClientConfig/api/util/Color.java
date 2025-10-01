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
package io.github.itzclient.AxolotlClientConfig.api.util;

// This is a simplified Color class. The original was much more complex.
public class Color {
    private int red, green, blue, alpha;
    
    public Color(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    
    public Color(int red, int green, int blue) { this(red, green, blue, 255); }
    public Color(int rgba) {
        this.alpha = (rgba >> 24) & 0xFF;
        this.red = (rgba >> 16) & 0xFF;
        this.green = (rgba >> 8) & 0xFF;
        this.blue = rgba & 0xFF;
    }

    public int getRed() { return red; }
    public int getGreen() { return green; }
    public int getBlue() { return blue; }
    public int getAlpha() { return alpha; }

    public int toInt() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    
    public Color withAlpha(int alpha) { return new Color(red, green, blue, alpha); }
    public Color immutable() { return this; }
    
    public static Color parse(String s) {
        return new Color(Integer.parseInt(s.substring(1), 16));
    }
}
