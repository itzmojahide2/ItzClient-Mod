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

import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.modules.hud.gui.component.HudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModuleWidget extends ClickableWidget {

    private static final int BACKGROUND_COLOR = 0xAA222222;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final Identifier PLACEHOLDER_ICON = Identifier.of("minecraft", "textures/item/compass_00.png");

    private final HudEntry module;
    private final ToggleButtonWidget toggle;

    public ModuleWidget(HudEntry module) {
        super(0, 0, 150, 40, Text.literal(module.getName()));
        this.module = module;
        
        this.toggle = new ToggleButtonWidget(0, 0, 20, 20, module.isEnabled());
        
        // Find the "enabled" option within the module's settings to link the toggle
        module.getCategory().getOptions().stream()
            .filter(o -> o.getName().equals("enabled") && o instanceof BooleanOption)
            .findFirst()
            .ifPresent(opt -> this.toggle.setToggled(((BooleanOption) opt).get()));
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        DrawUtil.drawRoundedRect(graphics, this.getX(), this.getY(), this.width, this.height, 5, BACKGROUND_COLOR);
        
        // In the future, you can give each module a unique icon
        graphics.drawTexture(PLACEHOLDER_ICON, this.getX() + 10, this.getY() + 12, 0, 0, 16, 16, 16, 16);
        
        graphics.drawText(
            MinecraftClient.getInstance().textRenderer,
            this.getMessage(),
            this.getX() + 35,
            this.getY() + (this.height - 8) / 2,
            TEXT_COLOR,
            true
        );
        
        this.toggle.setX(this.getX() + this.width - 25);
        this.toggle.setY(this.getY() + 10);
        this.toggle.render(graphics, mouseX, mouseY, delta);
    }
    
    @Override
    protected void updateNarration(NarrationMessageBuilder builder) {
        this.defaultNarrationMessage(builder);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.toggle.isMouseOver(mouseX, mouseY)) {
            this.toggle.onPress();
            // Update the module's "enabled" setting when the toggle is clicked
            module.getCategory().getOptions().stream()
                .filter(o -> o.getName().equals("enabled") && o instanceof BooleanOption)
                .findFirst()
                .ifPresent(opt -> ((BooleanOption) opt).set(this.toggle.isToggled()));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}