package io.github.steaf23.easymenulib;

import io.github.steaf23.easymenulib.util.EasyMenuTranslationKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public class EasyMenuLibrary
{
    private static JavaPlugin plugin;
    private static Function<EasyMenuTranslationKey, String> translateFunction;

    public static String test() {
        return "YEEET";
    }

    public static void setPlugin(final JavaPlugin plugin) {
        EasyMenuLibrary.plugin = plugin;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void setItemTranslation(Function<EasyMenuTranslationKey, String> translateFunction) {
        EasyMenuLibrary.translateFunction = translateFunction;
    }

    public static String translateKey(EasyMenuTranslationKey key) {
        return translateFunction.apply(key);
    }

}
