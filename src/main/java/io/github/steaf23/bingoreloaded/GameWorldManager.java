package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class GameWorldManager
{
    private static GameWorldManager INSTANCE;
    private Map<String, BingoSettings> templates;
    private Map<String, BingoGame> activeGames;

    public static GameWorldManager get()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new GameWorldManager();
        }
        return INSTANCE;
    }

    private GameWorldManager()
    {
        this.templates = new HashMap<>();
        this.activeGames = new HashMap<>();
    }

    public boolean createGame(String worldName, int maxTeamMembers)
    {
        if (doesGameWorldExist(worldName))
        {
            BingoMessage.log("An instance of Bingo already exists in world '" + worldName + "'!");
            return false;
        }

        templates.put(worldName, new BingoSettings());
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
            BingoMessage.log("Cannot start a game that doesn't exist, create it first using '/autobingo <world> create'!");
            return false;
        }

        if (isGameWorldActive(worldName))
        {
            BingoMessage.log("Could not start bingo because the game is already running on world '" + worldName + "'!");
            return false;
        }

        getGame(worldName).start(getGameSettings(worldName));
        return true;
    }

    public boolean endGame(String worldName)
    {
        if (!isGameWorldActive(worldName))
        {
            BingoMessage.log("Could not end bingo because no game was started on world '" + worldName + "'!");
            return false;
        }

        BingoGame game = activeGames.get(worldName);
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
        return isGameWorldActive(GameWorldManager.getWorldName(world));
    }

    public boolean doesGameWorldExist(String worldName)
    {
        return templates.containsKey(worldName);
    }

    public boolean doesGameWorldExist(World world)
    {
        return doesGameWorldExist(GameWorldManager.getWorldName(world));
    }
}
