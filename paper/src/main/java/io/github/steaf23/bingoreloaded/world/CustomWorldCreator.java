package io.github.steaf23.bingoreloaded.world;

import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformResources;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;


public class CustomWorldCreator
{
    public static @Nullable WorldHandle createWorld(Key worldKey, @Nullable Key generationSettingsResource) {
//        World world = CustomWorldCreator_V26_1_2.createBingoWorld(worldKey, generationSettingsResource);
//        if (world == null) {
//            return null;
//        }
//        return new WorldHandlePaper(world);
        return null;
    }

    private static String getWorldsFolder(PlatformResources resources) {
        return resources.getDataFolder().getPath().replace("\\", "/") + "/worlds/";
    }
}
