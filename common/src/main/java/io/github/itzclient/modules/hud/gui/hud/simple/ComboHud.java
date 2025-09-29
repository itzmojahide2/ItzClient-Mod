package io.github.itzclient.modules.hud.gui.hud.simple;

import io.github.itzclient.bridge.Platform;
import io.github.itzclient.bridge.events.Events;
import io.github.itzclient.bridge.util.AxoI18n;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

public class ComboHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "combohud");

    // --- State variables for tracking the combo ---
    private long lastHitTime = 0;
    private int lastTargetId = -1;
    private int comboCount = 0;

    @Override
    public void init() {
        // Listen for when the player attacks an entity
        Events.PLAYER_ATTACK.register((player, attackedEntity) -> {
            // Check if we are hitting the same target as before or starting a new combo
            if (attackedEntity.br$getNetId() == lastTargetId) {
                comboCount++;
            } else {
                // New target, start a new combo from 1
                lastTargetId = attackedEntity.br$getNetId();
                comboCount = 1;
            }
            lastHitTime = Platform.getMeasuringTimeMs();
        });

        // Listen for when the player takes damage
        Events.PLAYER_HURT.register((player, attacker) -> {
            // If the player gets hit, their combo is broken
            if (client.br$getPlayer() != null && player.br$getUuid().equals(client.br$getPlayer().br$getUuid())) {
                comboCount = 0;
                lastTargetId = -1;
            }
        });
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        // If 2 seconds (2000 ms) have passed since the last hit, reset the combo
        if (lastHitTime + 2000 < Platform.getMeasuringTimeMs()) {
            comboCount = 0;
        }

        if (comboCount == 0) {
            return AxoI18n.translate("combocounter.no_hits");
        }
        if (comboCount == 1) {
            return AxoI18n.translate("combocounter.one_hit");
        }
        return AxoI18n.translate("combocounter.hits", comboCount);
    }

    @Override
    public String getPlaceholder() {
        return AxoI18n.translate("combocounter.hits", 5);
    }
}