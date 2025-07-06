package io.github.steaf23.bingoreloaded.world;

import io.github.steaf23.bingoreloaded.lib.api.PlatformBridge;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

public class CustomWorldCreator
{
    public static WorldHandle createWorld(PlatformBridge platform, String worldName, @Nullable Key generationSettingsResource) {
        String worldFolder = getWorldsFolder(platform);
        return CustomWorldCreator_V1_21_4.createBingoWorld(worldFolder + worldName, generationSettingsResource);
    }

    private static String getWorldsFolder(PlatformBridge platform) {
        return platform.getDataFolder().getPath().replace("\\", "/") + "/worlds/";
    }
}
