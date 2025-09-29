package io.github.itzclient.modules.hud.gui.hud;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.itzclient.bridge.key.AxoKeybinding;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.TextHudEntry;
import io.github.itzclient.util.ClientColors;
import java.util.List;

public class KeystrokesHud extends TextHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "keystrokeshud");
    private final ColorOption pressedColor = new ColorOption("pressedColor", ClientColors.WHITE.withAlpha(150));
    private final ColorOption unpressedColor = new ColorOption("unpressedColor", ClientColors.BLACK.withAlpha(100));
    private final ColorOption pressedTextColor = new ColorOption("pressedTextColor", ClientColors.BLACK);
    private final ColorOption unpressedTextColor = new ColorOption("unpressedTextColor", ClientColors.WHITE);

    public KeystrokesHud() { super(54, 74, false); }
    @Override
    public AxoIdentifier getId() { return ID; }
    @Override
    public List<Option<?>> getConfigurationOptions() { List<Option<?>> o = super.getConfigurationOptions(); o.add(pressedColor); o.add(unpressedColor); o.add(pressedTextColor); o.add(unpressedTextColor); return o; }

    @Override
    public void renderComponent(AxoRenderContext context, float delta) {
        int x = getPos().x();
        int y = getPos().y();
        int keyWidth = 16;
        int keyHeight = 16;
        int spacing = 2;
        drawKey(context, "W", client.br$getKeybinds().br$getForwardKey(), x + keyWidth + spacing, y, keyWidth, keyHeight);
        drawKey(context, "A", client.br$getKeybinds().br$getLeftKey(), x, y + keyHeight + spacing, keyWidth, keyHeight);
        drawKey(context, "S", client.br$getKeybinds().br$getBackKey(), x + keyWidth + spacing, y + keyHeight + spacing, keyWidth, keyHeight);
        drawKey(context, "D", client.br$getKeybinds().br$getRightKey(), x + (keyWidth + spacing) * 2, y + keyHeight + spacing, keyWidth, keyHeight);
        drawKey(context, "LMB", client.br$getKeybinds().br$getAttackKey(), x, y + (keyHeight + spacing) * 2, (keyWidth * 2) + spacing, keyHeight);
        drawKey(context, "RMB", client.br$getKeybinds().br$getUseKey(), x + (keyWidth * 2) + (spacing * 2), y + (keyHeight + spacing) * 2, (keyWidth * 2) + spacing, keyHeight);
        drawKey(context, "SPACE", client.br$getKeybinds().br$getJumpKey(), x, y + (keyHeight + spacing) * 3, (keyWidth * 3) + (spacing * 2), keyHeight);
    }
    
    private void drawKey(AxoRenderContext context, String text, AxoKeybinding key, int x, int y, int width, int height) {
        boolean pressed = key.br$isPressed();
        Color bgColor = pressed ? pressedColor.get() : unpressedColor.get();
        Color txtColor = pressed ? pressedTextColor.get() : unpressedTextColor.get();
        context.br$fillRect(x, y, width, height, bgColor.toInt());
        context.br$drawCenteredString(text, x + width / 2, y + (height - 8) / 2, txtColor.toInt(), shadow.get());
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext context, float delta) { renderComponent(context, delta); }
}