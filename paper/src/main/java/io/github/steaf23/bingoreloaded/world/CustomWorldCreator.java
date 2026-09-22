package io.github.steaf23.bingoreloaded.world;

import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformResources;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;


public class CustomWorldCreator
{
    public static @Nullable WorldHandle createWorld(PlatformServer server, Key worldKey, @Nullable Key generationSettingsResource) {
        World world = CustomWorldCreator_V26_2.createBingoWorld(worldKey, generationSettingsResource);
        if (world == null) {
            return null;
        }
        return new WorldHandlePaper(server, world);
    }

    private static String getWorldsFolder(PlatformResources resources) {
        return resources.getDataFolder().getPath().replace("\\", "/") + "/worlds/";
    }
}
