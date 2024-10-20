package io.github.steaf23.playerdisplay.util;

import org.bukkit.plugin.java.JavaPlugin;

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

    public static void setupLogger(JavaPlugin plugin) {
        logger = new SimpleLog(new File(plugin.getDataFolder(), "log/debug.log"));
        ConsoleMessenger.log("Set up debug logger for plugin " + plugin.getName());
    }

    public static void stopLogger() {
        logger.close();
    }

    public static void setLoggingEnabled(boolean enabled) {
        useLogging = enabled;
        addLog(enabled ? "Enabled debug logging" : "Disabled debug logging");
    }
}
