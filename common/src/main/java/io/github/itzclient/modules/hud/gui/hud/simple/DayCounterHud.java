package io.github.itzclient.modules.hud.gui.hud.simple;

import io.github.itzclient.bridge.util.AxoI18n;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

public class DayCounterHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "daycounterhud");

    // --- Caching variables for optimization ---
    private String dayCountString = "0 Days";
    private int tickCounter = 0;

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
        // Update the day count once per second (every 20 ticks)
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;

            if (client.br$getWorld() == null) {
                this.dayCountString = getPlaceholder();
                return;
            }

            // The time of day is measured in ticks. 24000 ticks = 1 full day.
            long dayCount = client.br$getWorld().br$getTimeOfDay() / 24000L;
            this.dayCountString = AxoI18n.translate("daycounterhud.days", dayCount);
        }
    }

    @Override
    public String getValue() {
        // Return the cached day count string every frame.
        return this.dayCountString;
    }

    @Override
    public String getPlaceholder() {
        return AxoI18n.translate("daycounterhud.days", 35);
    }
}