package io.github.steaf23.bingoreloaded.lib.util;

import net.kyori.adventure.text.Component;

public class ConsoleMessenger
{
    public static void log(String message) {
        PlayerDisplay.getComponentLogger().info(
                PlayerDisplay.MINI_BUILDER.deserialize(message));
    }

    public static void warn(String message) {
        PlayerDisplay.getComponentLogger().warn(
                PlayerDisplay.MINI_BUILDER.deserialize(message));
    }

    public static void error(String message) {
        PlayerDisplay.getComponentLogger().error(
                PlayerDisplay.MINI_BUILDER.deserialize(message));
    }

    public static void log(String message, String source) {
        PlayerDisplay.getComponentLogger().info(
                Component.text("(" + source + "): ")
                        .append(PlayerDisplay.MINI_BUILDER.deserialize(message)));
    }

    public static void warn(String message, String source) {
        PlayerDisplay.getComponentLogger().warn(
                Component.text("(" + source + "): ")
                        .append(PlayerDisplay.MINI_BUILDER.deserialize(message)));
    }

    public static void error(String message, String source) {
        PlayerDisplay.getComponentLogger().error(
                Component.text("(" + source + "): ")
                        .append(PlayerDisplay.MINI_BUILDER.deserialize(message)));
    }

    public static void log(Component message) {
        PlayerDisplay.getComponentLogger().info(message);
    }

    public static void log(Component message, String source) {
        PlayerDisplay.getComponentLogger().info(
                Component.text("(" + source + "): ")
                        .append(message));
    }

    public static void log(Component message, Component source) {
        PlayerDisplay.getComponentLogger().info(
                Component.text("(").append(source).append(Component.text("): "))
                        .append(message));
    }

    public static void bug(String message, Class<?> source) {
        PlayerDisplay.getComponentLogger().error(
                PlayerDisplay.MINI_BUILDER.deserialize(message)
                        .append(Component.text("; Source: " + source.getName() + " (Please report!)")));
    }

    public static void bug(String message, Object source) {
        PlayerDisplay.getComponentLogger().error(
                PlayerDisplay.MINI_BUILDER.deserialize(message)
                        .append(Component.text("; Source: " + source.getClass().getName() + " (Please report!)")));
    }

    public static void bug(Component message, Object source) {
        PlayerDisplay.getComponentLogger().error(
                message.append(Component.text("; Source: " + source.getClass().getName() + " (Please report!)")));
    }
}
