package io.github.steaf23.bingoreloaded.world;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class CustomWorldCreator
{
    public static World createWorld(JavaPlugin plugin, String worldName, @Nullable NamespacedKey generationSettings) {
        String worldFolder = getWorldsFolder(plugin);
        return CustomWorldCreator_V1_21_4.createBingoWorld(worldFolder + worldName, generationSettings);
    }

    private static String getWorldsFolder(JavaPlugin plugin) {
        return plugin.getDataFolder().getPath().replace("\\", "/") + "/worlds/";
    }
}
