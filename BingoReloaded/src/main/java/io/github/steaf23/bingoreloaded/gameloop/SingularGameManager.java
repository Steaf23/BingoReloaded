package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.scoreboard.HUDRegistry;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SingularGameManager extends GameManager
{
    public SingularGameManager(@NotNull JavaPlugin plugin, BingoConfigurationData config, MenuBoard menuBoard, HUDRegistry hudRegistry) {
        super(plugin, config, menuBoard, hudRegistry);

        WorldGroup group = createWorldGroupFromExistingWorlds();
        if (group == null) {
            return;
        }

        BingoSession session = new BingoSession(menuBoard, hudRegistry, group, config);
        sessions.put(config.getOptionValue(BingoOptions.DEFAULT_WORLD_NAME), session);
    }

    // Don't create extra worlds...
    @Override
    public void setup(List<String> worldNames) {
    }

    @Override
    public boolean destroySession(String sessionName) {
        ConsoleMessenger.error("Cannot destroy session when using configuration singular!");
        return false;
    }

    @Override
    public boolean createSession(String sessionName) {
        ConsoleMessenger.error("Cannot create session when using configuration singular!");
        return false;
    }

    private WorldGroup createWorldGroupFromExistingWorlds() {
        String defaultWorldName = getGameConfig().getOptionValue(BingoOptions.DEFAULT_WORLD_NAME);
        World overworld = Bukkit.getWorld(defaultWorldName);
        World nether = Bukkit.getWorld(defaultWorldName + "_nether");
        World theEnd = Bukkit.getWorld(defaultWorldName + "_the_end");

        if (overworld == null) {
            ConsoleMessenger.error("Could not create world group from existing world; " + defaultWorldName + " does not exist. Make sure the world exists and reload the plugin.");
            return null;
        } else if (nether == null) {
            ConsoleMessenger.error("Could not create world group from existing world; " + defaultWorldName + "_nether does not exist. Make sure the world exists and reload the plugin.");
            return null;
        } else if (theEnd == null) {
            ConsoleMessenger.error("Could not create world group from existing world; " + defaultWorldName + "_the_end does not exist. Make sure the world exists and reload the plugin.");
            return null;
        }
        return new WorldGroup(defaultWorldName, overworld.getUID(), nether.getUID(), theEnd.getUID());
    }
}
