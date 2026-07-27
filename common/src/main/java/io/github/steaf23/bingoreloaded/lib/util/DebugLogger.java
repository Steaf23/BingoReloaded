package io.github.steaf23.bingoreloaded.lib.util;

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

    public static void setupLogger(File dataFolder, String extensionName) {
        logger = new SimpleLog(new File(dataFolder, "log/debug.log"));
        ConsoleMessenger.log("Set up debug logger for " + extensionName);
    }

    public static void stopLogger() {
        logger.close();
    }

    public static void setLoggingEnabled(boolean enabled) {
        useLogging = enabled;
        addLog(enabled ? "Enabled debug logging" : "Disabled debug logging");
    }
}
