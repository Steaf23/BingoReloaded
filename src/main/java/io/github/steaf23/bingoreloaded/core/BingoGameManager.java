package io.github.steaf23.bingoreloaded.core;

import io.github.steaf23.bingoreloaded.core.event.BingoEventListener;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class BingoGameManager
{
    private final BingoEventListener listener;
    private Map<String, BingoSession> sessions;

    public BingoGameManager()
    {
        this.listener = new BingoEventListener(this);
        this.sessions = new HashMap<>();
    }

    public BingoEventListener getListener()
    {
        return listener;
    }

    public boolean createSession(String worldName, String presetName)
    {
        if (doesSessionExist(worldName))
        {
            Message.log("An instance of Bingo already exists in world '" + worldName + "'!");
            return false;
        }

        BingoSession session = new BingoSession(worldName);
        sessions.put(worldName, session);
        return true;
    }

    public boolean destroySession(String worldName)
    {
        if (!doesSessionExist(worldName))
        {
            return false;
        }

        endGame(worldName);
        sessions.remove(worldName);
        return true;
    }

    public boolean startGame(String worldName)
    {
        if (!doesSessionExist(worldName))
        {
            Message.log("Cannot start a game that doesn't exist, create it first using '/autobingo <world> create'!");
            return false;
        }

        if (isGameWorldActive(worldName))
        {
            Message.log("Could not start bingo because the game is already running on world '" + worldName + "'!");
            return false;
        }

        sessions.get(worldName).startGame();
        return true;
    }

    public boolean endGame(String worldName)
    {
        if (!isGameWorldActive(worldName))
        {
            Message.log("Could not end bingo because no game was started on world '" + worldName + "'!");
            return false;
        }

        BingoSession session = sessions.get(worldName);
        session.endGame();
        return true;
    }

    public BingoSession getSession(String worldName)
    {
        if (sessions.containsKey(worldName))
        {
            return sessions.get(worldName);
        }
        return null;
    }

    public static String getWorldName(World world)
    {
        return world.getName()
                .replace("_nether", "")
                .replace("_the_end", "");
    }

    public boolean isGameWorldActive(String worldName)
    {
        return sessions.containsKey(worldName) && sessions.get(worldName).isRunning();
    }

    public boolean isGameWorldActive(World world)
    {
        return isGameWorldActive(BingoGameManager.getWorldName(world));
    }

    public boolean doesSessionExist(String worldName)
    {
        return sessions.containsKey(worldName);
    }

    public boolean doesSessionExist(World world)
    {
        return doesSessionExist(BingoGameManager.getWorldName(world));
    }
}
