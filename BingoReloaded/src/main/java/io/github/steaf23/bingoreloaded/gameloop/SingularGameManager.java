package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.data.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.scoreboard.HUDRegistry;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SingularGameManager extends GameManager
{
    public SingularGameManager(@NotNull JavaPlugin plugin, BingoConfigurationData config, MenuBoard menuBoard, HUDRegistry hudRegistry) {
        super(plugin, config, menuBoard, hudRegistry);

        WorldGroup group = createWorldGroupFromExistingWorlds();
        if (group == null) {
            return;
        }

        BingoSession session = new BingoSession(menuBoard, hudRegistry, group, config);
        sessions.put(config.defaultWorldName, session);
    }

    @Override
    public boolean destroySession(String sessionName) {
        ConsoleMessenger.error("This command is not available when using configuration singular!");
        return false;
    }

    @Override
    public boolean createSession(String sessionName) {
        ConsoleMessenger.error("This command is not available when using configuration singular!");
        return false;
    }

    private WorldGroup createWorldGroupFromExistingWorlds() {
        World overworld = Bukkit.getWorld(getGameConfig().defaultWorldName);
        World nether = Bukkit.getWorld(getGameConfig().defaultWorldName + "_nether");
        World theEnd = Bukkit.getWorld(getGameConfig().defaultWorldName + "_the_end");

        if (overworld == null) {
            ConsoleMessenger.error("Could not create world group from existing world; " + getGameConfig().defaultWorldName + " does not exist. Make sure the world exists and reload the plugin.");
            return null;
        } else if (nether == null) {
            ConsoleMessenger.error("Could not create world group from existing world; " + getGameConfig().defaultWorldName + "_nether does not exist. Make sure the world exists and reload the plugin.");
            return null;
        } else if (theEnd == null) {
            ConsoleMessenger.error("Could not create world group from existing world; " + getGameConfig().defaultWorldName + "_the_end does not exist. Make sure the world exists and reload the plugin.");
            return null;
        }
        return new WorldGroup(getGameConfig().defaultWorldName, overworld.getUID(), nether.getUID(), theEnd.getUID());
    }
}
