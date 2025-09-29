package io.github.itzclient;

import io.github.itzclient.api.API;
import io.github.itzclient.api.APIOptions;
import io.github.itzclient.api.StatusUpdateProviderImpl;
import io.github.itzclient.bridge.impl.Bridge;
import io.github.itzclient.config.ItzClientConfig;
import io.github.itzclient.config.ItzClientConfigCommon;
import io.github.itzclient.modules.ModuleLoader;
import io.github.itzclient.modules.auth.Auth;
import io.github.itzclient.modules.blur.MotionBlur;
import io.github.itzclient.modules.freelook.Freelook;
import io.github.itzclient.modules.hud.HudManager;
import io.github.itzclient.modules.hud.NametagHud;
import io.github.itzclient.modules.hud.gui.hud.KeystrokesHud;
import io.github.itzclient.modules.hud.gui.hud.simple.TargetHud;
import io.github.itzclient.modules.hypixel.HypixelMods;
import io.github.itzclient.modules.particles.Particles;
import io.github.itzclient.modules.renderOptions.BeaconBeam;
import io.github.itzclient.modules.rpc.DiscordRPC;
import io.github.itzclient.modules.screenshotUtils.ScreenshotUtils;
import io.github.itzclient.modules.scrollableTooltips.ScrollableTooltips;
import io.github.itzclient.modules.tablist.Tablist;
import io.github.itzclient.modules.tnttime.TntTime;
import io.github.itzclient.modules.zoom.Zoom;
import io.github.itzclient.util.FeatureDisabler;
import io.github.itzclient.util.Logger;
import io.github.itzclient.util.LoggerImpl;
import io.github.itzclient.util.notifications.Notifications;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class ItzClient extends ItzClientCommon implements ClientModInitializer {

    public static final HashMap<Identifier, Resource> runtimeResources = new HashMap<>();
    public static final Identifier badgeIcon = Identifier.of(MODID, "textures/badge.png");
    public static final Logger LOGGER = new LoggerImpl();

    private void addBuiltinModules() {
        // --- Original Axolotl Modules ---
        registerModule(Zoom.getInstance());
        registerModule(HudManager.getInstance());
        registerModule(HypixelMods.getInstance());
        registerModule(MotionBlur.getInstance());
        registerModule(ScrollableTooltips.getInstance());
        registerModule(DiscordRPC.getInstance());
        registerModule(Freelook.getInstance());
        registerModule(TntTime.getInstance());
        registerModule(Particles.getInstance());
        registerModule(ScreenshotUtils.getInstance());
        registerModule(BeaconBeam.getInstance());
        registerModule(Tablist.getInstance());
        registerModule(Auth.getInstance());
        registerModule(APIOptions.getInstance());

        // --- New ItzClient Modules ---
        registerModule(new TargetHud());
        registerModule(new KeystrokesHud());
        registerModule(NametagHud.getInstance());
    }

    private void addExternalModules() {
        ModuleLoader.loadExternalModules().forEach(this::registerModule);
    }

    @Override
    public void onInitializeClient() {
        Bridge.init();
        addBuiltinModules();
        addExternalModules();

        init(LOGGER, Notifications.getInstance());
        new API(new StatusUpdateProviderImpl(), APIOptions.getInstance());

        LOGGER.debug("Debug Output enabled, Logs will be quite verbose!");
        LOGGER.info("ItzClient Initialized");
    }

    @Override
    protected void initFeatureDisabler() {
        FeatureDisabler.init();
    }

    @Override
    protected ItzClientConfigCommon createConfig() {
        return new ItzClientConfig();
    }

    public static ItzClientConfig config() {
        return (ItzClientConfig) getInstance().getConfig();
    }
}