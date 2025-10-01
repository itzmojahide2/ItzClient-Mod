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
package io.github.itzclient.modules.zoom;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.itzclient.ItzClient;
import io.github.itzclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.FloatOption;
import io.github.itzclient.modules.AbstractModule;
import io.github.itzclient.util.Util;
import io.github.itzclient.util.keybinds.KeyBinds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;

public class Zoom extends AbstractModule {

    // --- Keybinds and Settings ---
    public static final KeyBind key = new KeyBind("key.zoom", InputUtil.KEY_C_CODE, "category.itzclient");
    public static final FloatOption zoomDivisor = new FloatOption("zoomDivisor", 4.0F, 1.0F, 16.0F);
    public static final FloatOption zoomSpeed = new FloatOption("zoomSpeed", 7.5F, 1.0F, 10.0F);
    public static final BooleanOption zoomScrolling = new BooleanOption("zoomScrolling", false);
    public static final BooleanOption decreaseSensitivity = new BooleanOption("decreaseSensitivity", true);
    public static final BooleanOption smoothCamera = new BooleanOption("smoothCamera", false);

    private static final Zoom Instance = new Zoom();

    // --- Internal State Variables ---
    private static boolean active;
    private static Double originalSensitivity;
    private static boolean originalSmoothCamera;
    private static double targetFactor = 1.0;
    private static double divisor;
    private static float lastAnimatedFactor = 1.0f;
    private static float animatedFactor = 1.0f;
    private static double lastReturnedFov;
    public final OptionCategory zoom = OptionCategory.create("zoom");

    public static Zoom getInstance() {
        return Instance;
    }

    public static double getFov(double currentFov, float tickDelta) {
        if (!active && animatedFactor == 1.0f) {
            return currentFov;
        }
        
        float smoothedFactor = (zoomSpeed.get() == 10.0f) ? (float) targetFactor : Util.lerp(lastAnimatedFactor, animatedFactor, tickDelta);
        
        double result = currentFov * smoothedFactor;

        if (lastReturnedFov != 0 && lastReturnedFov != result) {
            MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
        }
        lastReturnedFov = result;

        return result;
    }

    public static void update() {
        if (key.isPressed() && !active) {
            start();
        } else if (!key.isPressed() && active) {
            stop();
        }
    }

    private static void start() {
        active = true;
        setDivisor(zoomDivisor.get());
        
        originalSensitivity = MinecraftClient.getInstance().options.getMouseSensitivity().get();
        if (smoothCamera.get()) {
            originalSmoothCamera = MinecraftClient.getInstance().options.cinematicCamera;
            MinecraftClient.getInstance().options.cinematicCamera = true;
        }
        updateSensitivity();
    }

    private static void stop() {
        active = false;
        targetFactor = 1.0;
        
        MinecraftClient.getInstance().options.getMouseSensitivity().set(originalSensitivity);
        MinecraftClient.getInstance().options.cinematicCamera = originalSmoothCamera;
    }

    private static void setDivisor(double value) {
        divisor = value;
        targetFactor = 1.0 / value;
    }

    private static void updateSensitivity() {
        if (decreaseSensitivity.get() && originalSensitivity != null) {
            MinecraftClient.getInstance().options.getMouseSensitivity().set(originalSensitivity / (divisor * divisor));
        }
    }

    public static boolean scroll(double amount) {
        if (active && zoomScrolling.get() && amount != 0) {
            double newDivisor = Math.max(1.0, divisor + (amount > 0 ? 1.0 : -1.0));
            setDivisor(newDivisor);
            zoomDivisor.set((float) newDivisor);
            updateSensitivity();
            return true;
        }
        return false;
    }

    @Override
    public void init() {
        zoom.add(zoomDivisor);
        zoom.add(zoomSpeed);
        zoom.add(zoomScrolling);
        zoom.add(decreaseSensitivity);
        zoom.add(smoothCamera);
        KeyBinds.getInstance().register(key);

        ItzClient.config().rendering.add(zoom);

        KeyBinds.getInstance().registerWithSimpleAction(new KeyBind("key.zoom.increase", InputUtil.UNKNOWN_KEY.getKeyCode(), "category.itzclient"), () -> scroll(1.0));
        KeyBinds.getInstance().registerWithSimpleAction(new KeyBind("key.zoom.decrease", InputUtil.UNKNOWN_KEY.getKeyCode(), "category.itzclient"), () -> scroll(-1.0));
    }

    @Override
    public void tick() {
        lastAnimatedFactor = animatedFactor;
        animatedFactor += (targetFactor - animatedFactor) * (zoomSpeed.get() / 10.0F);
    }
}