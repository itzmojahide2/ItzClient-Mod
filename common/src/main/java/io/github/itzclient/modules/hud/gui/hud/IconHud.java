package io.github.itzclient.modules.hud.gui.hud;

import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.render.AxoSprites;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.BoxHudEntry;

public class IconHud extends BoxHudEntry {

    // The ID now correctly points to the ItzClient namespace
    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "iconhud");

    public IconHud() {
        super(16, 16, false); // The icon is 16x16 pixels and has no background by default
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public void renderComponent(AxoRenderContext ctx, float delta) {
        // Set the color to white to ensure the texture is not tinted
        ctx.br$glColor4(1.0f, 1.0f, 1.0f, 1.0f);
        ctx.br$glEnableBlend();
        
        // AxoSprites.BADGE is a reference to the main client icon (icon.png)
        ctx.br$drawTexture(getPos().x(), getPos().y(), width, height, AxoSprites.BADGE);
        
        ctx.br$glDisableBlend();
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext ctx, float delta) {
        // The placeholder is the same as the actual render for this simple module
        renderComponent(ctx, delta);
    }
}