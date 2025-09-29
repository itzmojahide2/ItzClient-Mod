package io.github.itzclient.modules.hud.gui.hud.simple;

import io.github.itzclient.bridge.AxoMinecraftClient;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

public class FPSHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "fpshud");
    private String fpsString = "0 FPS";
    private int tickCounter = 0;

    public FPSHud() { super(); }
    @Override
    public AxoIdentifier getId() { return ID; }
    @Override
    public boolean tickable() { return true; }

    @Override
    public void tick() {
        tickCounter++;
        if (tickCounter >= 5) {
            tickCounter = 0;
            fpsString = AxoMinecraftClient.getCurrentFps() + " FPS";
        }
    }

    @Override
    public String getValue() { return fpsString; }
    @Override
    public String getPlaceholder() { return "120 FPS"; }
}