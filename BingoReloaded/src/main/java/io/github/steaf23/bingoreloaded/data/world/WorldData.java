package io.github.steaf23.bingoreloaded.data.world;

import io.github.steaf23.bingoreloaded.data.helper.ResourceFileHelper;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class WorldData
{
    /**
     * Removes all worlds in the pluginFolder/worlds folder
     *
     * @return false if 1 or more worlds could not be removed for any reason
     */
    public static boolean clearWorlds(@NotNull JavaPlugin plugin) {
        String worldFolder = getWorldsFolder(plugin);
        File worldsFolderDir = FileUtils.getFile(worldFolder);
        for (File f : worldsFolderDir.listFiles(File::isDirectory)) {
            if (f.equals(worldsFolderDir)) continue;

            String worldName = f.getName();
            if (!destroyWorld(plugin, worldName)) {
                continue;
            }
        }
        return true;
    }

    /**
     * Creates a world group, creating all worlds if they do not exist yet.
     * If worlds by the same name exist, this will just construct a world group with the pre-existing worlds
     * @param plugin
     * @param worldName
     * @return created WorldGroup
     */
    public static WorldGroup createWorldGroup(@NotNull JavaPlugin plugin, String worldName) {
        World overworld = createWorld(plugin, worldName, World.Environment.NORMAL);
        World nether = createWorld(plugin, worldName + "_nether", World.Environment.NETHER);
        World end = createWorld(plugin, worldName + "_the_end", World.Environment.THE_END);
        return new WorldGroup(worldName, overworld.getUID(), nether.getUID(), end.getUID());
    }

    public static @Nullable WorldGroup getWorldGroup(@NotNull JavaPlugin plugin, String worldName) {
        World overworld = Bukkit.getWorld(getWorldsFolder(plugin) + worldName);
        World nether = Bukkit.getWorld(getWorldsFolder(plugin) + worldName + "_nether");
        World theEnd = Bukkit.getWorld(getWorldsFolder(plugin) + worldName + "_the_end");

        if (overworld == null) {
            Message.error("Could not fetch world group; " + worldName + " does not exist. Make sure the world exists and reload the plugin.");
            return null;
        } else if (nether == null) {
            Message.error("Could not fetch world group; " + worldName + "_nether does not exist. Make sure the world exists and reload the plugin.");
            return null;
        } else if (theEnd == null) {
            Message.error("Could not fetch world group; " + worldName + "_the_end does not exist. Make sure the world exists and reload the plugin.");
            return null;
        }
        WorldGroup group = new WorldGroup(worldName, overworld.getUID(), nether.getUID(), theEnd.getUID());
        return group;
    }

    /**
     * !Also removes the worlds from the plugin folder permanently!
     * @param plugin
     * @param worldGroup
     * @return true if the worlds in the world group could be destroyed correctly
     */
    public static boolean destroyWorldGroup(@NotNull JavaPlugin plugin, @NotNull WorldGroup worldGroup) {
        boolean success = destroyWorld(plugin, worldGroup.worldName());
        success = success && destroyWorld(plugin, worldGroup.worldName() + "_nether");
        success = success && destroyWorld(plugin, worldGroup.worldName() + "_the_end");
        return success;
    }

    private static String getWorldsFolder(JavaPlugin plugin) {
        return plugin.getDataFolder().getPath().replace("\\", "/") + "/worlds/";
    }

    private static World createWorld(@NotNull JavaPlugin plugin, String worldName, World.Environment environment) {
        String worldFolder = getWorldsFolder(plugin);
        WorldCreator creator = new WorldCreator(worldFolder + worldName);
        creator.environment(environment);
        return Bukkit.createWorld(creator);
    }

    private static boolean destroyWorld(@NotNull JavaPlugin plugin, String worldName) {
        String worldsFolder = getWorldsFolder(plugin);

        World bukkitWorld = Bukkit.getWorld(worldsFolder + worldName);
        if (bukkitWorld == null)
        {
            if (!ResourceFileHelper.deleteFolderRecurse(worldsFolder + worldName))
            {
                Message.error("Could not remove folder for " + worldName + ", cannot find the folder of this world (it might already be removed) or the folder could not be accessed. (Please report!)");
            }
            return false;
        }
        UUID worldId = bukkitWorld.getUID();
        boolean worldUnloaded = Bukkit.unloadWorld(bukkitWorld, false);

        // If world was not unloaded, it means that either it does not exist, it was already unloaded, or there are still players in the world
        World unloadedWorld = Bukkit.getWorld(worldId);
        boolean stillLoaded = unloadedWorld != null || !worldUnloaded;
        if (stillLoaded) {
            // Players are still in the world, it could not be unloaded
            Message.error("Could not remove " + worldName + ", world could not be unloaded (Maybe there are still players present?).");
            return false;
        }

        if (!ResourceFileHelper.deleteFolderRecurse(worldsFolder + worldName))
        {
            Message.error("Could not remove folder for " + worldName + ", cannot find the folder of this world (it might already be removed) or the folder could not be accessed. (Please report!)");
        }
        return true;
    }
}
