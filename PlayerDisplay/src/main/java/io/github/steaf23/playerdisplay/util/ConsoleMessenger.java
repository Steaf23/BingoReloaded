package io.github.steaf23.playerdisplay.util;

import io.github.steaf23.playerdisplay.PlayerDisplay;
import net.kyori.adventure.text.Component;

public class ConsoleMessenger
{
    public static void log(String message) {
        PlayerDisplay.getPlugin().getComponentLogger().info(
                PlayerDisplay.MINI_BUILDER.deserialize(message));
    }

    public static void warn(String message) {
        PlayerDisplay.getPlugin().getComponentLogger().warn(
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

    public static void bug(String message, Class<?> source) {
        PlayerDisplay.getPlugin().getComponentLogger().error(
                PlayerDisplay.MINI_BUILDER.deserialize(message)
                        .append(Component.text("; Source: " + source.getName() + " (Please report!)")));
    }

    public static void bug(String message, Object source) {
        PlayerDisplay.getPlugin().getComponentLogger().error(
                PlayerDisplay.MINI_BUILDER.deserialize(message)
                        .append(Component.text("; Source: " + source.getClass().getName() + " (Please report!)")));
    }

    public static void bug(Component message, Object source) {
        PlayerDisplay.getPlugin().getComponentLogger().error(
                message.append(Component.text("; Source: " + source.getClass().getName() + " (Please report!)")));
    }
}
