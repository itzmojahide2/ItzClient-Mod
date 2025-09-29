package io.github.itzclient.modules.hud.gui.hud.simple;

import io.github.itzclient.bridge.util.AxoI18n;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

public class PlayerCountHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "playercounthud");

    // This variable will store the calculated string, so we don't have to recalculate it every frame.
    private String playerCountString = "0 Players";
    private int tickCounter = 0;

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    // By returning true here, we tell the HudManager to call our tick() method.
    @Override
    public boolean tickable() {
        return true;
    }

    // This method is now called by the HudManager 20 times per second (once per game tick).
    @Override
    public void tick() {
        // We use a counter to ensure our logic only runs once every 20 ticks (which is 1 second).
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0; // Reset the counter for the next second.

            // The expensive logic now only runs ONCE PER SECOND.
            if (client.br$getWorld() != null) {
                int count = client.br$getWorld().br$getPlayers().size();
                this.playerCountString = count + " " + AxoI18n.translate("players");
            } else {
                this.playerCountString = getPlaceholder();
            }
        }
    }

    // This method is still called every frame to draw the HUD, but now it is extremely fast.
    @Override
    public String getValue() {
        // It simply returns the pre-calculated string. No calculations are done here.
        return this.playerCountString;
    }

    @Override
    public String getPlaceholder() {
        return "10 " + AxoI18n.translate("players");
    }
}
