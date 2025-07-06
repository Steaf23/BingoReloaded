package io.github.steaf23.bingoreloaded.lib.util;

import io.github.steaf23.bingoreloaded.lib.api.PlatformBridge;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import net.kyori.adventure.text.Component;

public class ConsoleMessenger
{
    private static final PlatformBridge PLATFORM = PlatformResolver.get();

    public static void log(String message) {
        PLATFORM.getComponentLogger().info(
                ComponentUtils.MINI_BUILDER.deserialize(message));
    }

    public static void warn(String message) {
        PLATFORM.getComponentLogger().warn(
                ComponentUtils.MINI_BUILDER.deserialize(message));
    }

    public static void error(String message) {
        PLATFORM.getComponentLogger().error(
                ComponentUtils.MINI_BUILDER.deserialize(message));
    }

    public static void log(String message, String source) {
        PLATFORM.getComponentLogger().info(
                Component.text("(" + source + "): ")
                        .append(ComponentUtils.MINI_BUILDER.deserialize(message)));
    }

    public static void warn(String message, String source) {
        PLATFORM.getComponentLogger().warn(
                Component.text("(" + source + "): ")
                        .append(ComponentUtils.MINI_BUILDER.deserialize(message)));
    }

    public static void error(String message, String source) {
        PLATFORM.getComponentLogger().error(
                Component.text("(" + source + "): ")
                        .append(ComponentUtils.MINI_BUILDER.deserialize(message)));
    }

    public static void log(Component message) {
        PLATFORM.getComponentLogger().info(message);
    }

    public static void log(Component message, String source) {
        PLATFORM.getComponentLogger().info(
                Component.text("(" + source + "): ")
                        .append(message));
    }

    public static void log(Component message, Component source) {
        PLATFORM.getComponentLogger().info(
                Component.text("(").append(source).append(Component.text("): "))
                        .append(message));
    }

    public static void bug(String message, Class<?> source) {
        PLATFORM.getComponentLogger().error(
                ComponentUtils.MINI_BUILDER.deserialize(message)
                        .append(Component.text("; Source: " + source.getName() + " (Please report!)")));
    }

    public static void bug(String message, Object source) {
        PLATFORM.getComponentLogger().error(
                ComponentUtils.MINI_BUILDER.deserialize(message)
                        .append(Component.text("; Source: " + source.getClass().getName() + " (Please report!)")));
    }

    public static void bug(Component message, Object source) {
        PLATFORM.getComponentLogger().error(
                message.append(Component.text("; Source: " + source.getClass().getName() + " (Please report!)")));
    }
}
