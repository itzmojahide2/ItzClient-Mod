package io.github.itzclient.modules.hud.gui.hud.simple;

import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public class PingHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "pinghud");

    // --- Settings for this module ---
    private final IntegerOption refreshDelay = new IntegerOption("refreshTime", 2, 1, 15); // Default to 2 seconds

    // --- State variables for optimization ---
    private final MutableInt currentServerPing = new MutableInt(0);
    private int tickCounter = 0;

    public PingHud() {
        super();
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        // Use a tick counter to control the update frequency based on user settings.
        tickCounter++;
        if (tickCounter >= refreshDelay.get() * 20) { // refreshDelay is in seconds, 20 ticks per second
            tickCounter = 0;
            
            // Get the player and their network connection handler
            if (client.br$getPlayer() != null && client.br$getPlayer().networkHandler != null) {
                // Get the player's own entry from the player list
                var playerListEntry = client.br$getPlayer().networkHandler.getPlayerListEntry(client.br$getPlayer().getUuid());
                if (playerListEntry != null) {
                    // Update the ping value from the player list entry
                    currentServerPing.setValue(playerListEntry.getLatency());
                }
            } else if (client.br$isLocalServer()) {
                // If in singleplayer, ping is effectively 0.
                currentServerPing.setValue(0);
            }
        }
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(refreshDelay);
        return options;
    }

    @Override
    public String getValue() {
        return currentServerPing.getValue() + " ms";
    }

    @Override
    public String getPlaceholder() {
        return "20 ms";
    }
}