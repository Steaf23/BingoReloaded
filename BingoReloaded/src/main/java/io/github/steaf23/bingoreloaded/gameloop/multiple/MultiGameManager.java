package io.github.steaf23.bingoreloaded.gameloop.multiple;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.PlayerSerializationData;
import io.github.steaf23.bingoreloaded.data.world.WorldManager;
import io.github.steaf23.bingoreloaded.event.BingoEventListener;
import io.github.steaf23.bingoreloaded.gameloop.SessionManager;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

public class MultiGameManager implements SessionManager
{
    private final BingoEventListener eventListener;
    private Map<String, BingoSession> sessions;
    private final ConfigData config;
    private final PlayerSerializationData playerData;

    private final BingoReloaded plugin;

    public MultiGameManager(BingoReloaded plugin) {
        this.plugin = plugin;
        this.config = plugin.config();
        this.eventListener = new BingoEventListener(world -> getSessionFromWorld(world), config.disableAdvancements, config.disableStatistics);
        this.sessions = new HashMap<>();
        this.playerData = new PlayerSerializationData();

        Bukkit.getPluginManager().registerEvents(eventListener, plugin);
    }

    @Override
    public MenuManager getMenuManager() {
        return null;
    }

    @Override
    public ConfigData getConfig() {
        return config;
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(eventListener);
    }

    @Override
    public int sessionCount() {
        return sessions.size();
    }

    public boolean createSession(String sessionName) {
        if (sessions.containsKey(sessionName)) {
            Message.log("An instance of Bingo already exists in world '" + sessionName + "'!");
            return false;
        }

        BingoSession session = new BingoSession(this, getMenuManager(), WorldManager.getOrCreateWorldGroup(plugin, sessionName), config, playerData);
        sessions.put(sessionName, session);
        return true;
    }

    public boolean destroySession(String sessionName) {
        if (!sessions.containsKey(sessionName)) {
            return false;
        }

        endGame(sessionName);
        sessions.remove(sessionName);
        return true;
    }

    public boolean startGame(String sessionName) {
        if (!sessions.containsKey(sessionName)) {
            Message.log("Cannot start a game that doesn't exist, create it first using '/autobingo <world> create'!");
            return false;
        }

        if (isGameWorldActive(sessionName)) {
            Message.log("Could not start bingo because the game is already running on world '" + sessionName + "'!");
            return false;
        }

        sessions.get(sessionName).startGame();
        return true;
    }

    public boolean endGame(String sessionName) {
        if (!isGameWorldActive(sessionName)) {
            Message.log("Could not end bingo because no game was started on world '" + sessionName + "'!");
            return false;
        }

        BingoSession session = sessions.get(sessionName);
        session.endGame();
        return true;
    }

    public BingoSession getSession(String sessionName) {
        if (sessions.containsKey(sessionName)) {
            return sessions.get(sessionName);
        }
        return null;
    }

    public BingoSession getSessionFromWorld(World world) {
        for (String session : sessions.keySet())
        {
            BingoSession s = sessions.get(session);
            if (s.ownsWorld(world)) {
                return s;
            }
        }
        return null;
    }

    public boolean isGameWorldActive(String sessionName) {
        return sessions.containsKey(sessionName) && sessions.get(sessionName).isRunning();
    }
}
