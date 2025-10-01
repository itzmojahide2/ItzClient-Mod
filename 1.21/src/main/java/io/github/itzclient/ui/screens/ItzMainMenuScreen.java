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
package io.github.itzclient.ui.screens;

import io.github.itzclient.ItzClient;
import io.github.itzclient.ui.widgets.StyledButtonWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.button.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItzMainMenuScreen extends Screen {

    private static final Identifier LOGO = Identifier.of("itzclient", "icon.png");
    private static final Identifier MOD_MENU_ICON = Identifier.of("minecraft", "textures/gui/sprites/widget/server_list.png");

    public ItzMainMenuScreen() {
        super(Text.literal("ItzClient Main Menu"));
    }

    @Override
    protected void init() {
        super.init();
        int buttonWidth = 204;
        int buttonHeight = 20;
        int buttonSpacing = 4;
        int y = this.height / 2 - (buttonHeight * 2);

        this.addDrawableChild(new StyledButtonWidget(this.width / 2 - buttonWidth / 2, y, buttonWidth, buttonHeight, Text.translatable("menu.singleplayer"), button -> 
            this.client.setScreen(new SelectWorldScreen(this))
        ));
        y += buttonHeight + buttonSpacing;
        this.addDrawableChild(new StyledButtonWidget(this.width / 2 - buttonWidth / 2, y, buttonWidth, buttonHeight, Text.translatable("menu.multiplayer"), button -> 
            this.client.setScreen(new MultiplayerScreen(this))
        ));
        
        int iconButtonX = this.width / 2 + buttonWidth / 2 + buttonSpacing;
        this.addDrawableChild(new TexturedButtonWidget(iconButtonX, y, buttonHeight, buttonHeight, MOD_MENU_ICON, button -> {
            this.client.setScreen(new HudEditorScreen());
        }));

        y += buttonHeight + buttonSpacing;
        this.addDrawableChild(new StyledButtonWidget(this.width / 2 - buttonWidth / 2, y, buttonWidth, buttonHeight, Text.translatable("menu.options"), button -> 
            this.client.setScreen(new OptionsScreen(this, this.client.options))
        ));
        y += buttonHeight + buttonSpacing + 10;
        this.addDrawableChild(new StyledButtonWidget(this.width / 2 - buttonWidth / 2, y, buttonWidth, buttonHeight, Text.translatable("menu.quit"), button -> 
            this.client.scheduleStop()
        ));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);
        graphics.drawTexture(LOGO, this.width / 2 - 32, this.height / 2 - 120, 0, 0, 64, 64, 64, 64);
        graphics.drawCenteredShadowedText(this.textRenderer, "ITZCLIENT", this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        String version = "ItzClient " + ItzClient.VERSION;
        graphics.drawCenteredShadowedText(this.textRenderer, version, this.width / 2, this.height - 20, 0xAAAAAA);
        super.render(graphics, mouseX, mouseY, delta);
    }
}