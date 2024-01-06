package io.github.steaf23.bingoreloaded.data.world;

import io.github.steaf23.bingoreloaded.util.Message;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SimpleWorldFactory implements WorldFactory
{
    private final String worldsFolder;

    public SimpleWorldFactory(JavaPlugin plugin) {
        this.worldsFolder = getWorldsFolder(plugin);
    }

    /**
     * Removes all worlds in the pluginFolder/worlds folder
     *
     * @return false if 1 or more worlds could not be removed for any reason
     */
    @Override
    public boolean clearWorlds() {
        File worldsFolderDir = FileUtils.getFile(worldsFolder);
        for (File f : FileUtils.listFilesAndDirs(worldsFolderDir, FileFilterUtils.directoryFileFilter(), null)) {
            if (f.equals(worldsFolderDir)) continue;

            World bukkitWorld = Bukkit.getWorld(f.getName());
            if (bukkitWorld == null) {
                return false;
            }
            if (!removeWorld(bukkitWorld)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeWorld(@NotNull World world) {
        Message.log("Removing " + world.getName());
        if (!world.getName().startsWith(worldsFolder)) {
            Message.error("Cannot remove world not created by this plugin!");
            return false;
        }
        String worldName = world.getName();
        UUID worldId = world.getUID();
        boolean worldUnloaded = Bukkit.unloadWorld(world, false);

        // If world was not unloaded, it means that either it does not exist, it was already unloaded, or there are still players in the world
        World unloadedWorld = Bukkit.getWorld(worldId);
        boolean stillLoaded = unloadedWorld != null;
        if (stillLoaded) {
            // Players are still in the world, it could not be unloaded
            Message.error("Could not remove " + worldName + ", world could not be unloaded (Maybe there are still players present?)");
            return false;
        }

        File folder = FileUtils.getFile(worldName);
        try {
            FileUtils.deleteDirectory(folder);
        } catch (IOException e) {
            Message.error("Could not remove " + worldName + ", cannot find the folder of this world (it might already be removed) or the folder could not be accessed!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public World createOverworld(@NotNull String worldName) {
        WorldCreator creator = new WorldCreator(worldsFolder + worldName);
        return Bukkit.createWorld(creator);
    }

    @Override
    public World createNether(@NotNull String worldName) {
        WorldCreator creator = new WorldCreator(worldsFolder + worldName + "_nether");
        creator.environment(World.Environment.NETHER);
        return Bukkit.createWorld(creator);
    }

    @Override
    public World createTheEnd(@NotNull String worldName) {
        WorldCreator creator = new WorldCreator(worldsFolder + worldName + "_the_end");
        creator.environment(World.Environment.THE_END);
        return Bukkit.createWorld(creator);
    }

    @Override
    public boolean linkNetherPortals(World overworld, World nether) {
        return false;
    }

    @Override
    public boolean linkEndPortals(World overworld, World end) {
        return false;
    }

    public static String stripPath(JavaPlugin plugin, @NotNull String fullName) {
        return fullName.replace(getWorldsFolder(plugin), "");
    }

    private static String getWorldsFolder(JavaPlugin plugin) {
        return plugin.getDataFolder().getPath().replace("\\", "/") + "/worlds/";
    }
}
