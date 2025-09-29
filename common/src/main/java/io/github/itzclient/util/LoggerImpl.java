package io.github.itzclient.util;

import io.github.itzclient.ItzClient; // RENAMED
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

public class LoggerImpl implements Logger {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("ItzClient"); // RENAMED
    private static final String prefix = FabricLoader.getInstance().isDevelopmentEnvironment() ? "" : "(ItzClient) "; // RENAMED

    public void info(String msg, Object... args) {
        LOGGER.info(prefix + msg, args);
    }

    public void warn(String msg, Object... args) {
        LOGGER.warn(prefix + msg, args);
    }

    public void error(String msg, Object... args) {
        LOGGER.error(prefix + msg, args);
    }

    public void debug(String msg, Object... args) {
        // Correctly references the config from the rebranded main class
        if (ItzClient.config().debugLogOutput.get()) {
            LOGGER.info(prefix + "[DEBUG] " + msg, args);
        }
    }
}
