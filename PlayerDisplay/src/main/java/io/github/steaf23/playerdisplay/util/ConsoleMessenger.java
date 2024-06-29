package io.github.steaf23.playerdisplay.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

public class ConsoleMessenger
{
    public static String pluginPrefix = "";

    public static void log(String message) {
        Bukkit.getLogger().info(pluginPrefix + message);
    }

    public static void warn(String message) {
        Bukkit.getLogger().warning(pluginPrefix + message);
    }

    public static void error(String message) {
        Bukkit.getLogger().severe(pluginPrefix + message);
    }

    public static void log(String message, String source) {
        Bukkit.getLogger().info(pluginPrefix + NamedTextColor.GREEN + "(" + source + "): " + NamedTextColor.WHITE + message);
    }

    public static void warn(String message, String source) {
        Bukkit.getLogger().warning(pluginPrefix + "(" + source + "): " + message);
    }

    public static void error(String message, String source) {
        Bukkit.getLogger().severe(pluginPrefix + "(" + source + "): " + message);
    }

    public static void log(Component message) {
        Bukkit.getLogger().info(pluginPrefix + LegacyComponentSerializer.legacySection().serialize(message));
    }

    public static void log(Component message, String source) {
        Bukkit.getLogger().info(pluginPrefix + NamedTextColor.GREEN + "(" + source + "): " + NamedTextColor.WHITE + message);
    }

    public static void log(Component message, Component source) {
        Bukkit.getLogger().info(pluginPrefix + NamedTextColor.GREEN + "(" + source + "): " + NamedTextColor.WHITE + message);
    }

    public static void bug(String message, Object source) {
        Bukkit.getLogger().severe(pluginPrefix + message + " in " + source.getClass().getName() + "(Please report!)");
    }
}
