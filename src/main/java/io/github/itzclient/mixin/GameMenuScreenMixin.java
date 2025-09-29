package io.github.itzclient.mixin;

import io.github.itzclient.ui.screens.HudEditorScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    private static final Identifier MOD_MENU_ICON = Identifier.of("minecraft", "textures/gui/sprites/widget/server_list.png");

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("RETURN"))
    private void addModMenuButton(CallbackInfo ci) {
        this.addDrawableChild(new TexturedButtonWidget(
            this.width / 2 + 104,
            this.height / 4 + 72 + -16,
            20, 20,
            MOD_MENU_ICON,
            (button) -> this.client.setScreen(new HudEditorScreen())
        ));
    }
}