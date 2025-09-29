package io.github.itzclient;

import io.github.itzclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.itzclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.itzclient.AxolotlClientConfig.api.ui.ConfigUI;
import io.github.itzclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.itzclient.AxolotlClientConfig.impl.managers.VersionedJsonConfigManager;
import io.github.itzclient.api.API;
import io.github.itzclient.bridge.events.Events;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.bridge.util.AxoProfiler;
import io.github.itzclient.config.ItzClientConfigCommon;
import io.github.itzclient.config.profiles.ProfileAware;
import io.github.itzclient.config.profiles.Profiles;
import io.github.itzclient.modules.Module;
import io.github.itzclient.modules.hud.ClickInputTracker;
import io.github.itzclient.util.Logger;
import io.github.itzclient.util.OSUtil;
import io.github.itzclient.util.notifications.NotificationProvider;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ItzClientCommon {
    public static final String MODID = "itzclient";
    public static final AxoIdentifier BADGE_PATH = AxoIdentifier.of(MODID, "textures/badge.png");

    public static Path resolveConfigFile(String file) {
        return FabricLoader.getInstance().getConfigDir().resolve(MODID).resolve(file);
    }

    public static Path resolveProfileConfigFile(String file) {
        return Profiles.getInstance().resolveProfileFile(file);
    }

    public static final boolean NVG_SUPPORTED = OSUtil.getOS() != OSUtil.OperatingSystem.OTHER &&
        !Objects.requireNonNullElse(System.getenv("TMPDIR"), "").contains("Android") && !FabricLoader.getInstance().isModLoaded("vulkanmod");

    public static final String VERSION = FabricLoader.getInstance()
        .getModContainer("itzclient-common")
        .orElseThrow()
        .getMetadata()
        .getVersion()
        .getFriendlyString();

    public static final String GAME_VERSION = FabricLoader.getInstance()
        .getModContainer("minecraft")
        .orElseThrow()
        .getMetadata()
        .getVersion()
        .getFriendlyString();

    private static ItzClientCommon instance;

    private ItzClientConfigCommon config;
    private Logger logger;
    private NotificationProvider notificationProvider;
    private JsonConfigManager configManager;
    private boolean initializing = false;
    public final List<Module> modules = new ArrayList<>();

    protected ItzClientCommon() {}

    public ItzClientConfigCommon getConfig() { return config; }
    public ConfigManager getConfigManager() { return configManager; }
    public Logger getLogger() { return logger; }
    public NotificationProvider getNotificationProvider() { return notificationProvider; }
    public static ItzClientCommon getInstance() { return instance; }

    private void addBuiltinCommonModules() {
        registerModule(ClickInputTracker.getInstance());
    }

    private void earlyModuleInit() {
        modules.forEach(Module::init);
    }

    private void lateModuleInit() {
        modules.forEach(Module::lateInit);
    }

    private void initConfig() {
        var configFile = getMainConfigFile();
        if (Files.notExists(configFile)) {
            // Check for the old AxolotlClient config file to migrate it
            var legacy = new Path[]{resolveConfigFile("axolotlclient.json"), FabricLoader.getInstance().getConfigDir().resolve("AxolotlClient.json")};
            for (Path p : legacy) {
                try {
                    if (Files.exists(p)) Files.move(p, configFile);
                } catch (IOException e) {
                    logger.warn("Failed to move legacy config file, it might get reset!", e);
                }
            }
        }
        configManager = new VersionedJsonConfigManager(configFile, config.getConfig(), 5, (oldVersion, newVersion, config, json) -> json);
        AxolotlClientConfig.getInstance().register(configManager);
        configManager.suppressName("x");
        configManager.suppressName("y");
        configManager.suppressName(config.hidden.getName());
    }

    protected final void init(Logger logger, NotificationProvider provider) {
        addBuiltinCommonModules();
        initializing = true;
        instance = this;
        this.logger = logger;
        Profiles.getInstance().loadProfiles();
        this.notificationProvider = provider;
        config = createConfig();
        earlyModuleInit();
        initConfig();
        ConfigUI.getInstance().runWhenLoaded(() -> {
            ConfigUI.getInstance().addWidget("vanilla", "graphics", "io.github.itzclient.util.options.vanilla.AxoGraphicsWidget");
            ConfigUI.getInstance().addWidget("rounded", "graphics", "io.github.itzclient.util.options.rounded.AxoGraphicsWidget");
            lateModuleInit();
        });
        Events.TICK.register(() -> {
            AxoProfiler.get().br$push("ItzClient");
            modules.forEach(Module::tick);
            AxoProfiler.get().br$pop();
        });
        initFeatureDisabler();
        Events.CLIENT_STOP.register(() -> API.getInstance().shutdown());
    }

    protected final void registerModule(Module module) {
        modules.add(module);
    }

    protected abstract void initFeatureDisabler();
    protected abstract ItzClientConfigCommon createConfig();

    public void saveConfig() {
        getConfigManager().save();
        for (Module m : modules) {
            if (m instanceof ProfileAware p) p.saveConfig();
        }
    }

    public Path getMainConfigFile() {
        var path = resolveProfileConfigFile("itzclient.json");
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            getLogger().warn("Failed to create config directory, config may not be saved correctly!", e);
        }
        return path;
    }

    public void reloadConfig() {
        configManager.setFile(getMainConfigFile());
        configManager.load();
        for (Module m : modules) {
            if (m instanceof ProfileAware p) p.reloadConfig();
        }
        lateModuleInit();
        API.getInstance().restart();
    }
}
