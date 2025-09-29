package io.github.itzclient.modules.hud;

import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.modules.hud.gui.hud.KeystrokesHud;
import io.github.itzclient.modules.hud.gui.hud.PackDisplayHud;
import io.github.itzclient.modules.hud.gui.hud.PlayerHud;
import io.github.itzclient.modules.hud.gui.hud.vanilla.*;
import io.github.itzclient.ui.screens.HudEditorScreen; // CORRECTED IMPORT for our new UI
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

public class HudManager extends HudManagerCommon {
    @Getter
    private final static HudManager instance = new HudManager();

    @Override
    protected void openScreen() {
        // This now opens our new, clean HUD editor screen.
        MinecraftClient.getInstance().setScreen(new HudEditorScreen());
    }

    @Override
    protected void addExtraHud() {
        // These are the HUDs specific to this version of Minecraft
        add(new ActionBarHud());
        add(new BossBarHud());
        add(new CrosshairHud());
        add(new DebugCountersHud());
        add(new HotbarHUD());
        add(new ScoreboardHud());
        // KeystrokesHud is already in ItzClient.java, but this would be a place for it too
        add(new PackDisplayHud());
        add(new PlayerHud());
    }

    @Override
    public void render(AxoRenderContext context, float delta) {
        final var mc = MinecraftClient.getInstance();
        mc.getProfiler().push("Hud render");
        if(!(mc.currentScreen instanceof HudEditorScreen)) {
            super.render(context, delta);
        }
        mc.getProfiler().pop();
    }
}