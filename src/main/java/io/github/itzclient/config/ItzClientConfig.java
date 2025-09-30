package io.github.itzclient.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.texture.NativeImage;
import io.github.itzclient.ItzClientCommon;
import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.itzclient.AxolotlClientConfig.api.util.Color;
import io.github.itzclient.AxolotlClientConfig.impl.options.*;
import io.github.itzclient.config.screen.CreditsScreen;
import io.github.itzclient.config.screen.ProfilesScreen;
import io.github.itzclient.mixin.OverlayTextureAccessor;
import io.github.itzclient.util.keybinds.KeyBinds;
import io.github.itzclient.util.options.ForceableBooleanOption;
import io.github.itzclient.util.options.GenericOption;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.texture.NativeImageBackedTexture;

import java.util.ArrayList;
import java.util.List;

public class ItzClientConfig extends ItzClientConfigCommon {

    // --- Options ---
    public final BooleanOption showOwnNametag = new BooleanOption("showOwnNametag", false);
    public final BooleanOption useShadows = new BooleanOption("useShadows", false);
    public final BooleanOption nametagBackground = new BooleanOption("nametagBackground", true);
    public final ForceableBooleanOption timeChangerEnabled = new ForceableBooleanOption("enabled", false);
    public final IntegerOption customTime = new IntegerOption("time", 0, 0, 24000);
    public final BooleanOption customSky = new BooleanOption("customSky", true);
    public final BooleanOption dynamicFOV = new BooleanOption("dynamicFov", true);
    public final ForceableBooleanOption fullBright = new ForceableBooleanOption("fullBright", false);
    public final BooleanOption removeVignette = new BooleanOption("removeVignette", false);
    public final ForceableBooleanOption lowFire = new ForceableBooleanOption("lowFire", false);
    public final BooleanOption lowShield = new BooleanOption("lowShield", false);
    public final ColorOption hitColor = new ColorOption("hitColor", new Color(255, 0, 0, 77), value -> {
        try {
            NativeImageBackedTexture texture = ((OverlayTextureAccessor) MinecraftClient.getInstance().gameRenderer.getOverlayTexture()).axolotlclient$getTexture();
            NativeImage nativeImage = texture.getImage();
            if (nativeImage != null) {
                int color = 255 - value.getAlpha();
                color = (color << 8) + value.getBlue();
                color = (color << 8) + value.getGreen();
                color = (color << 8) + value.getRed();
                for (int i = 0; i < 8; ++i) { for (int j = 0; j < 8; ++j) { nativeImage.setPixelColor(j, i, color); } }
                RenderSystem.activeTexture(33985);
                texture.bindTexture();
                nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
                RenderSystem.activeTexture(33984);
            }
        } catch (Exception ignored) {}
    });
    public final BooleanOption minimalViewBob = new BooleanOption("minimalViewBob", false);
    public final BooleanOption noHurtCam = new BooleanOption("noHurtCam", false);
    public final BooleanOption flatItems = new BooleanOption("flatItems", false);
    public final BooleanOption noRain = new BooleanOption("noRain", false);
    public final BooleanOption showBrandingWatermark = new BooleanOption("showBrandingWatermark", true);

    // --- Universal HUD Settings ---
    public final OptionCategory hudSettings = OptionCategory.create("hudSettings");
    public final IntegerOption hudCornerRadius = new IntegerOption("hudCornerRadius", 0, 0, 10);
    public final IntegerOption hudBackgroundOpacity = new IntegerOption("hudBackgroundOpacity", 100, 0, 255);

    // --- Categories ---
    public final OptionCategory general = OptionCategory.create("general");
    public final OptionCategory rendering = OptionCategory.create("rendering");
    public final OptionCategory outlines = OptionCategory.create("blockOutlines");
    public final OptionCategory timeChanger = OptionCategory.create("timeChanger");

    @Getter
    private final List<Option<?>> options = new ArrayList<>();

    public ItzClientConfig() {
        config.add(general);
        config.add(rendering);
        config.add(hudSettings);
        rendering.add(outlines);
        rendering.add(timeChanger);

        general.add(showBrandingWatermark);
        general.add(new GenericOption("profiles.title", "profiles.configure", () -> MinecraftClient.getInstance().setScreen(new ProfilesScreen(MinecraftClient.getInstance().currentScreen))), false);
        
        hudSettings.add(hudCornerRadius);
        hudSettings.add(hudBackgroundOpacity);

        rendering.add(customSky, dynamicFOV, fullBright, removeVignette, lowFire, lowShield, hitColor, minimalViewBob, noHurtCam, flatItems, noRain);
        
        timeChanger.add(timeChangerEnabled);
        timeChanger.add(customTime);

        outlines.add(new BooleanOption("enabled", false));
        outlines.add(new ColorOption("color", Color.parse("#DD000000")));
        
        var toggleFullbright = new KeyBind("toggle_fullbright", -1, "category.itzclient");
        KeyBinds.getInstance().registerWithSimpleAction(toggleFullbright, fullBright::toggle);
    }
}