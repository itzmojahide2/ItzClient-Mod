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
package io.github.itzclient.modules.hud.gui.hud.simple;

import io.github.itzclient.bridge.entity.AxoLivingEntity;
import io.github.itzclient.bridge.entity.AxoPlayer;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;
import io.github.itzclient.util.ClientColors;
import net.minecraft.client.hit.EntityHitResult;
import net.minecraft.client.hit.HitResult;
import net.minecraft.entity.LivingEntity;
import java.text.DecimalFormat;

public class TargetHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "targethud");
    private static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("#0.0");
    private boolean shouldRender = false;
    private String targetName = "Target";
    private float health = 20.0f;
    private float maxHealth = 20.0f;
    private String distance = "0.0m";
    private int tickCounter = 0;

    public TargetHud() { super(120, 40, true); }
    @Override
    public AxoIdentifier getId() { return ID; }
    @Override
    public String getPlaceholder() { return ""; }
    @Override
    public String getValue() { return ""; }
    @Override
    public boolean tickable() { return true; }

    @Override
    public void tick() {
        tickCounter++;
        if (tickCounter >= 2) {
            tickCounter = 0;
            if (client.br$getPlayer() == null || client.br$getWorld() == null) {
                shouldRender = false;
                return;
            }
            HitResult crosshairTarget = client.crosshairTarget;
            AxoLivingEntity target = null;
            if (crosshairTarget != null && crosshairTarget.getType() == HitResult.Type.ENTITY) {
                if (((EntityHitResult) crosshairTarget).getEntity() instanceof LivingEntity) {
                    target = (AxoLivingEntity) ((EntityHitResult) crosshairTarget).getEntity();
                }
            }
            if (target != null) {
                shouldRender = true;
                targetName = ((AxoPlayer) target).br$getName();
                health = ((LivingEntity) target).getHealth();
                maxHealth = ((LivingEntity) target).getMaxHealth();
                double dist = client.br$getPlayer().br$getPos().dist(target.br$getPos());
                distance = DISTANCE_FORMAT.format(dist) + "m";
            } else {
                shouldRender = false;
            }
        }
    }

    @Override
    public void renderComponent(AxoRenderContext context, float delta) {
        if (!shouldRender && !isPlaceholder()) { return; }
        String name = isPlaceholder() ? "ItzPlayer" : targetName;
        float currentHealth = isPlaceholder() ? 12.5f : health;
        float maximumHealth = isPlaceholder() ? 20.0f : maxHealth;
        String dist = isPlaceholder() ? "4.2m" : distance;
        int x = getPos().x();
        int y = getPos().y();
        context.br$drawString(name, x + 5, y + 5, textColor.get().toInt(), shadow.get());
        int distanceWidth = context.br$getFont().br$getWidth(dist);
        context.br$drawString(dist, x + getWidth() - 5 - distanceWidth, y + 5, 0xAAAAAA, shadow.get());
        context.br$fillRect(x + 5, y + 18, getWidth() - 10, 10, 0x80000000);
        float healthPercentage = currentHealth / maximumHealth;
        int healthBarWidth = (int) ((getWidth() - 10) * healthPercentage);
        context.br$fillRect(x + 5, y + 18, healthBarWidth, 10, ClientColors.SELECTOR_RED.toInt());
        String healthText = (int)currentHealth + " / " + (int)maximumHealth;
        context.br$drawCenteredString(healthText, x + getWidth() / 2, y + 19, 0xFFFFFF, true);
    }
}