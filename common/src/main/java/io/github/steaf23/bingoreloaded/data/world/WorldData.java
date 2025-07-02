package io.github.steaf23.bingoreloaded.data.world;

import io.github.steaf23.bingoreloaded.lib.api.Extension;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.data.helper.ResourceFileHelper;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.world.CustomWorldCreator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class WorldData
{
    private final Extension extension;
    private final Key generationOptions;

    public WorldData(Extension extension, @Nullable Key generationOptions) {
        this.extension = extension;
        this.generationOptions = generationOptions;
    }

    /**
     * Removes all worlds in the pluginFolder/worlds folder
     *
     * @return false if 1 or more worlds could not be removed for any reason
     */
    public boolean clearWorlds() {
        String worldFolder = getWorldsFolder(extension);
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
            if (destroyWorld(extension, worldName)) {
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
        WorldHandle overworld = CustomWorldCreator.createWorld(extension, worldName, generationOptions);
        if (overworld == null) {
            overworld = createWorld(extension, worldName, World.Environment.NORMAL);
        }
        WorldHandle nether = createWorld(extension, worldName + "_nether", World.Environment.NETHER);
        WorldHandle end = createWorld(extension, worldName + "_the_end", World.Environment.THE_END);
        return new WorldGroup(worldName, overworld.getUniqueId(), nether.getUniqueId(), end.getUniqueId());
    }

    public @Nullable WorldGroup getWorldGroup(String worldName) {
        WorldHandle overworld = Bukkit.getWorld(getWorldsFolder(extension) + worldName);
        WorldHandle nether = Bukkit.getWorld(getWorldsFolder(extension) + worldName + "_nether");
        WorldHandle theEnd = Bukkit.getWorld(getWorldsFolder(extension) + worldName + "_the_end");

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
        return new WorldGroup(worldName, overworld.getUniqueId(), nether.getUniqueId(), theEnd.getUniqueId());
    }

    /**
     * !Also removes the worlds from the plugin folder permanently!
     * @return true if the worlds in the world group could be destroyed correctly
     */
    public boolean destroyWorldGroup(@NotNull WorldGroup worldGroup) {
        boolean success = destroyWorld(extension, worldGroup.worldName());
        success = success && destroyWorld(extension, worldGroup.worldName() + "_nether");
        success = success && destroyWorld(extension, worldGroup.worldName() + "_the_end");
        return success;
    }

    private static String getWorldsFolder(MinecraftExtension extension) {
        return extension.getDataFolder().getPath().replace("\\", "/") + "/worlds/";
    }

    private static WorldHandle createWorld(@NotNull MinecraftExtension extension, String worldName, World.Environment environment) {
        String worldFolder = getWorldsFolder(extension);
        WorldCreator creator = new WorldCreator(worldFolder + worldName);
        creator.environment(environment);
        return Bukkit.createWorld(creator);
    }

    private static boolean destroyWorld(@NotNull MinecraftExtension extension, String worldName) {
        String worldsFolder = getWorldsFolder(extension);

        WorldHandle bukkitWorld = Bukkit.getWorld(worldsFolder + worldName);
        if (bukkitWorld == null)
        {
            if (!ResourceFileHelper.deleteFolderRecurse(worldsFolder + worldName))
            {
                ConsoleMessenger.bug("Could not remove folder for " + worldName + ", cannot find the folder of this world (it might already be removed) or the folder could not be accessed", plugin);
            }
            return false;
        }
        UUID worldId = bukkitWorld.getUniqueId();
        boolean worldUnloaded = Bukkit.unloadWorld(bukkitWorld, false);

        // If world was not unloaded, it means that either it does not exist, it was already unloaded, or there are still players in the world
        WorldHandle unloadedWorld = Bukkit.getWorld(worldId);
        boolean stillLoaded = unloadedWorld != null || !worldUnloaded;
        if (stillLoaded) {
            // Players are still in the world, it could not be unloaded
            ConsoleMessenger.error("Could not remove " + worldName + ", world could not be unloaded (Maybe there are still players present?).");
            return false;
        }

        if (!ResourceFileHelper.deleteFolderRecurse(worldsFolder + worldName))
        {
            ConsoleMessenger.bug("Could not remove folder for " + worldName + ", cannot find the folder of this world (it might already be removed) or the folder could not be accessed", plugin);
        }
        return true;
    }
}
