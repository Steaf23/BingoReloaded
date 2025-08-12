package io.github.steaf23.bingoreloaded.data.world;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.ResourceFileHelper;
import io.github.steaf23.bingoreloaded.lib.api.DimensionType;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldOptions;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class WorldData
{
    private final ServerSoftware platform;
    private final Key generationOptions;

    public WorldData(ServerSoftware platform, @Nullable Key generationOptions) {
        this.platform = platform;
        this.generationOptions = generationOptions;
    }

    /**
     * Removes all worlds in the pluginFolder/worlds folder
     *
     * @return false if 1 or more worlds could not be removed for any reason
     */
    public boolean clearWorlds() {
        String worldFolder = getWorldsFolder();
        File worldsFolderDir = FileUtils.getFile(worldFolder);
        if (!worldsFolderDir.exists()) {
            if (!worldsFolderDir.mkdirs()) {
                return false;
            }
        }

        int removeCount = 0;
        for (File f : worldsFolderDir.listFiles(File::isDirectory)) {
            if (f.equals(worldsFolderDir)) continue;

            String worldName = f.getName();
            if (destroyWorld(worldName)) {
                removeCount++;
            }
        }
        ConsoleMessenger.log(Component.text("Removed " + removeCount + " bingo worlds on startup").color(NamedTextColor.LIGHT_PURPLE));
        return true;
    }

    /**
     * Creates a world group, creating all worlds if they do not exist yet.
     * If worlds by the same name exist, this will just construct a world group with the pre-existing worlds
     * @return created WorldGroup
     */
    public WorldGroup createWorldGroup(String worldName) {
        WorldHandle overworld = BingoReloaded.runtime().createBingoWorld(worldName, generationOptions);
        if (overworld == null) {
            overworld = createWorld(worldName, DimensionType.OVERWORLD);
        }
        WorldHandle nether = createWorld( worldName + "_nether", DimensionType.NETHER);
        WorldHandle end = createWorld( worldName + "_the_end", DimensionType.THE_END);
        return new WorldGroup(platform, worldName, overworld.uniqueId(), nether.uniqueId(), end.uniqueId());
    }

    public @Nullable WorldGroup getWorldGroup(String worldName) {
        WorldHandle overworld = platform.getWorld(getWorldsFolder() + worldName);
        WorldHandle nether = platform.getWorld(getWorldsFolder() + worldName + "_nether");
        WorldHandle theEnd = platform.getWorld(getWorldsFolder() + worldName + "_the_end");

        if (overworld == null) {
            ConsoleMessenger.error("Could not fetch world group; " + worldName + " does not exist. Make sure the world exists and reload the plugin.");
            return null;
        } else if (nether == null) {
            ConsoleMessenger.error("Could not fetch world group; " + worldName + "_nether does not exist. Make sure the world exists and reload the plugin.");
            return null;
        } else if (theEnd == null) {
            ConsoleMessenger.error("Could not fetch world group; " + worldName + "_the_end does not exist. Make sure the world exists and reload the plugin.");
            return null;
        }
        return new WorldGroup(platform, worldName, overworld.uniqueId(), nether.uniqueId(), theEnd.uniqueId());
    }

    /**
     * !Also removes the worlds from the plugin folder permanently!
     * @return true if the worlds in the world group could be destroyed correctly
     */
    public boolean destroyWorldGroup(@NotNull WorldGroup worldGroup) {
        boolean success = destroyWorld(worldGroup.worldName());
        success = success && destroyWorld(worldGroup.worldName() + "_nether");
        success = success && destroyWorld(worldGroup.worldName() + "_the_end");
        return success;
    }

    private String getWorldsFolder() {
        return platform.getDataFolder().getPath().replace("\\", "/") + "/worlds/";
    }

    private WorldHandle createWorld(String worldName, DimensionType dimension) {
        String worldFolder = getWorldsFolder();
        WorldOptions options = new WorldOptions(worldFolder + worldName, dimension);
        return platform.createWorld(options);
    }

    private boolean destroyWorld(String worldName) {
        String worldsFolder = getWorldsFolder();

        WorldHandle bukkitWorld = platform.getWorld(worldsFolder + worldName);
        if (bukkitWorld == null)
        {
            if (!ResourceFileHelper.deleteFolderRecurse(worldsFolder + worldName))
            {
                ConsoleMessenger.bug("Could not remove folder for " + worldName + ", cannot find the folder of this world (it might already be removed) or the folder could not be accessed", platform);
            }
            return false;
        }
        UUID worldId = bukkitWorld.uniqueId();
        boolean worldUnloaded = platform.unloadWorld(bukkitWorld, false);

        // If world was not unloaded, it means that either it does not exist, it was already unloaded, or there are still players in the world
        WorldHandle unloadedWorld = platform.getWorld(worldId);
        boolean stillLoaded = unloadedWorld != null || !worldUnloaded;
        if (stillLoaded) {
            // Players are still in the world, it could not be unloaded
            ConsoleMessenger.error("Could not remove " + worldName + ", world could not be unloaded (Maybe there are still players present?).");
            return false;
        }

        if (!ResourceFileHelper.deleteFolderRecurse(worldsFolder + worldName))
        {
            ConsoleMessenger.bug("Could not remove folder for " + worldName + ", cannot find the folder of this world (it might already be removed) or the folder could not be accessed", platform);
        }
        return true;
    }
}
