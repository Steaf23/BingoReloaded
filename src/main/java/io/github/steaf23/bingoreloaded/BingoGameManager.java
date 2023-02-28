package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.core.BingoGame;
import io.github.steaf23.bingoreloaded.core.BingoSettings;
import io.github.steaf23.bingoreloaded.core.event.BingoEndedEvent;
import io.github.steaf23.bingoreloaded.core.event.BingoEventListener;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class BingoGameManager
{
    private static BingoGameManager INSTANCE;
    private Map<String, BingoSettings> templates;
    private Map<String, BingoGame> activeGames;
    private final BingoEventListener listener;

    public static BingoGameManager get()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new BingoGameManager();
        }
        return INSTANCE;
    }

    private BingoGameManager()
    {
        this.templates = new HashMap<>();
        this.activeGames = new HashMap<>();
        this.listener = new BingoEventListener(this);
    }

    public BingoEventListener getListener()
    {
        return listener;
    }

    public boolean createGame(String worldName, int maxTeamMembers)
    {
        if (doesGameWorldExist(worldName))
        {
            Message.log("An instance of Bingo already exists in world '" + worldName + "'!");
            return false;
        }

        templates.put(worldName, new BingoSettings(worldName));
        getGameSettings(worldName).maxTeamSize = maxTeamMembers;
        BingoGame newGame = new BingoGame(worldName);
        activeGames.put(worldName, newGame);
        return true;
    }

    public boolean destroyGame(String worldName)
    {
        if (!doesGameWorldExist(worldName))
        {
            return false;
        }

        if (isGameWorldActive(worldName))
            endGame(worldName);
        
        templates.remove(worldName);
        activeGames.remove(worldName);
        return true;
    }

    public boolean startGame(String worldName)
    {
        if (!doesGameWorldExist(worldName))
        {
            Message.log("Cannot start a game that doesn't exist, create it first using '/autobingo <world> create'!");
            return false;
        }

        if (isGameWorldActive(worldName))
        {
            Message.log("Could not start bingo because the game is already running on world '" + worldName + "'!");
            return false;
        }

        getGame(worldName).start(getGameSettings(worldName));
        return true;
    }

    public boolean endGame(String worldName)
    {
        if (!isGameWorldActive(worldName))
        {
            Message.log("Could not end bingo because no game was started on world '" + worldName + "'!");
            return false;
        }

        BingoGame game = activeGames.get(worldName);
        var event = new BingoEndedEvent(game.getGameTime(), null, worldName);
        Bukkit.getPluginManager().callEvent(event);
        game.end();
        return true;
    }

    public BingoGame getGame(String worldName)
    {
        if (activeGames.containsKey(worldName))
        {
            return activeGames.get(worldName);
        }
        return null;
    }

    public BingoGame getActiveGame(String worldName)
    {
        if (isGameWorldActive(worldName))
        {
            return activeGames.get(worldName);
        }
        return null;
    }

    public BingoSettings getGameSettings(String worldName)
    {
        if (!templates.containsKey(worldName))
        {
            return null;
        }
        return templates.get(worldName);
    }

    public static String getWorldName(World world)
    {
        return world.getName()
                .replace("_nether", "")
                .replace("_the_end", "");
    }

    public boolean isGameWorldActive(String worldName)
    {
        return doesGameWorldExist(worldName) && activeGames.containsKey(worldName) && activeGames.get(worldName).isInProgress();
    }

    public boolean isGameWorldActive(World world)
    {
        return isGameWorldActive(BingoGameManager.getWorldName(world));
    }

    public boolean doesGameWorldExist(String worldName)
    {
        return templates.containsKey(worldName);
    }

    public boolean doesGameWorldExist(World world)
    {
        return doesGameWorldExist(BingoGameManager.getWorldName(world));
    }
}
