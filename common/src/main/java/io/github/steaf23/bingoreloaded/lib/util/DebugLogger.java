package io.github.steaf23.bingoreloaded.lib.util;

import io.github.steaf23.bingoreloaded.lib.api.PlatformBridge;

import java.io.File;

public class DebugLogger
{
    private static SimpleLog logger;
    private static boolean useLogging = false;

    public static void addLog(String message) {
        if (useLogging && logger != null) {
            logger.log(message);
        }
    }

    public static void setupLogger(PlatformBridge platform) {
        logger = new SimpleLog(new File(platform.getDataFolder(), "log/debug.log"));
        ConsoleMessenger.log("Set up debug logger for " + platform.getExtensionInfo().name());
    }

    public static void stopLogger() {
        logger.close();
    }

    public static void setLoggingEnabled(boolean enabled) {
        useLogging = enabled;
        addLog(enabled ? "Enabled debug logging" : "Disabled debug logging");
    }
}
