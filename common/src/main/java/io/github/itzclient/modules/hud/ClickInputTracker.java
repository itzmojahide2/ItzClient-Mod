package io.github.itzclient.modules.hud;

import io.github.itzclient.bridge.Platform;
import io.github.itzclient.bridge.events.Events;
import io.github.itzclient.bridge.key.AxoKeys;
import io.github.itzclient.modules.AbstractCommonModule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ClickInputTracker extends AbstractCommonModule {
    @Getter
    private static final ClickInputTracker instance = new ClickInputTracker();

    public final ClickList leftMouse = new ClickList();
    public final ClickList leftBind = new ClickList();
    public final ClickList rightMouse = new ClickList();
    public final ClickList rightBind = new ClickList();

    @Override
    public void init() {
        Events.KEY_INPUT.register(key -> {
            if (key.equals(client.br$getKeybinds().br$getAttackKey().br$getBoundKey())) {
                leftBind.click();
            } else if (key.equals(client.br$getKeybinds().br$getUseKey().br$getBoundKey())) {
                rightBind.click();
            }

            if (key.equals(AxoKeys.MOUSE_LEFT)) {
                leftMouse.click();
            } else if (key.equals(AxoKeys.MOUSE_RIGHT)) {
                rightMouse.click();
            }
        });
    }

    @Override
    public void tick() {
        // Update all lists every tick to remove old clicks
        leftMouse.update();
        leftBind.update();
        rightMouse.update();
        rightBind.update();
    }

    public static class ClickList {
        private final List<Long> clicks;

        public ClickList() {
            clicks = new ArrayList<>();
        }

        public void update() {
            // Remove any clicks that are older than 1 second (1000 milliseconds)
            clicks.removeIf((clickTime) -> Platform.getMeasuringTimeMs() - clickTime > 1000);
        }

        public void click() {
            clicks.add(Platform.getMeasuringTimeMs());
        }

        public int clicks() {
            return clicks.size();
        }
    }
}