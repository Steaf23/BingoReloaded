package io.github.steaf23.playerdisplay.util;

import io.github.steaf23.playerdisplay.PlayerDisplay;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ConsoleMessenger
{
    public static void log(String message) {
        PlayerDisplay.getPlugin().getComponentLogger().info(
                PlayerDisplay.MINI_BUILDER.deserialize(message));
    }

    public static void warn(String message) {
        PlayerDisplay.getPlugin().getComponentLogger().info(
                PlayerDisplay.MINI_BUILDER.deserialize(message));
    }

    public static void error(String message) {
        PlayerDisplay.getPlugin().getComponentLogger().error(
                PlayerDisplay.MINI_BUILDER.deserialize(message));
    }

    public static void log(String message, String source) {
        PlayerDisplay.getPlugin().getComponentLogger().info(
                Component.text("(" + source + "): ")
                        .append(PlayerDisplay.MINI_BUILDER.deserialize(message)));
    }

    public static void warn(String message, String source) {
        PlayerDisplay.getPlugin().getComponentLogger().warn(
                Component.text("(" + source + "): ")
                        .append(PlayerDisplay.MINI_BUILDER.deserialize(message)));
    }

    public static void error(String message, String source) {
        PlayerDisplay.getPlugin().getComponentLogger().error(
                Component.text("(" + source + "): ")
                        .append(PlayerDisplay.MINI_BUILDER.deserialize(message)));
    }

    public static void log(Component message) {
        PlayerDisplay.getPlugin().getComponentLogger().info(message);
    }

    public static void log(Component message, String source) {
        PlayerDisplay.getPlugin().getComponentLogger().info(
                Component.text("(" + source + "): ")
                        .append(message));
    }

    public static void log(Component message, Component source) {
        PlayerDisplay.getPlugin().getComponentLogger().info(
                Component.text("(").append(source).append(Component.text("): "))
                        .append(message));
    }

    public static void bug(String message, Object source) {
        PlayerDisplay.getPlugin().getComponentLogger().error(
                PlayerDisplay.MINI_BUILDER.deserialize(message)
                        .append(Component.text(" in " + source.getClass().getName() + "(Please report!)")));
    }

    public static void bug(Component message, Object source) {
        PlayerDisplay.getPlugin().getComponentLogger().error(
                message.append(Component.text(" in " + source.getClass().getName() + "(Please report!)")));
    }
}
