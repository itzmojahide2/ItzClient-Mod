package io.github.itzclient.modules.hud.gui.hud.simple;

import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.itzclient.bridge.Platform;
import io.github.itzclient.bridge.entity.AxoEntity;
import io.github.itzclient.bridge.events.Events;
import io.github.itzclient.bridge.math.Vec3;
import io.github.itzclient.bridge.util.AxoI18n;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.SimpleTextHudEntry;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class ReachHud extends SimpleTextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "reachhud");

    // --- Settings for this module ---
    private final IntegerOption decimalPlaces = new IntegerOption("decimalplaces", 2, 0, 15);

    // --- State variables ---
    private String currentDist;
    private long lastHitTime = 0;

    private static double getAttackDistance(AxoEntity attacker, AxoEntity target) {
        // A more accurate way to calculate reach is from eye position to the target's bounding box
        Vec3 attackerEyePos = attacker.br$getPos().add(new Vec3(0, attacker.br$getEyeHeight(), 0));
        Vec3 targetPos = target.br$getPos();
        // This is a simplified distance; more complex calculations could check bounding box corners
        return attackerEyePos.dist(targetPos);
    }

    @Override
    public void init() {
        // This logic is event-driven, which is very efficient.
        Events.PLAYER_ATTACK.register((attackingPlayer, attackedEntity) -> {
            if (client.br$getPlayer() != null && attackingPlayer.br$getUuid().equals(client.br$getPlayer().br$getUuid())) {
                double distance = getAttackDistance(attackingPlayer, attackedEntity);
                
                // Format the distance based on the user's settings
                StringBuilder formatPattern = new StringBuilder("0");
                if (decimalPlaces.get() > 0) {
                    formatPattern.append(".");
                    formatPattern.append("0".repeat(decimalPlaces.get()));
                }
                DecimalFormat formatter = new DecimalFormat(formatPattern.toString());
                formatter.setRoundingMode(RoundingMode.HALF_UP);

                currentDist = formatter.format(distance) + " " + AxoI18n.translate("blocks");
                lastHitTime = Platform.getMeasuringTimeMs();
            }
        });
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(decimalPlaces);
        return options;
    }

    @Override
    public String getValue() {
        // If 2 seconds have passed since the last hit, hide the display.
        if (lastHitTime + 2000 < Platform.getMeasuringTimeMs()) {
            currentDist = null;
        }
        
        if (currentDist == null) {
            return "0 " + AxoI18n.translate("blocks");
        }
        return currentDist;
    }

    @Override
    public String getPlaceholder() {
        return "3.45 " + AxoI18n.translate("blocks");
    }
}