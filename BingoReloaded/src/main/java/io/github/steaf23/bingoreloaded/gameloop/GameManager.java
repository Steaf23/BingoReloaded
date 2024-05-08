package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.PlayerSerializationData;
import io.github.steaf23.bingoreloaded.data.world.WorldData;
import io.github.steaf23.bingoreloaded.event.BingoEventListener;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.menu.Menu;
import io.github.steaf23.easymenulib.menu.MenuBoard;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager
{
    private static final String SINGULAR_WORLDNAME = "world";
    private final boolean singular;
    private final JavaPlugin plugin;
    private final ConfigData config;
    private final MenuBoard menuBoard;

    private final Map<String, BingoSession> sessions;
    private final PlayerSerializationData playerData;
    private final BingoEventListener eventListener;

    public GameManager(@NotNull JavaPlugin plugin, boolean singular, ConfigData config, MenuBoard menus) {
        this.plugin = plugin;
        this.config = config;
        this.menuBoard = menus;
        this.singular = singular;

        this.sessions = new HashMap<>();
        this.playerData = new PlayerSerializationData();
        this.eventListener = new BingoEventListener(this::getSessionFromWorld, config.disableAdvancements, config.disableStatistics);

        if (singular)
        {
            BingoSession session = new BingoSession(this, menuBoard, WorldData.getOrCreateWorldGroup(plugin, SINGULAR_WORLDNAME), config, playerData);
            sessions.put(SINGULAR_WORLDNAME, session);
        }
        Bukkit.getPluginManager().registerEvents(eventListener, plugin);
    }

    public boolean createSession(String sessionName) {
        if (singular)
        {
            Message.error("This command is not available when using configuration singular!");
            return false;
        }

        if (sessions.containsKey(sessionName)) {
            Message.log("An instance of Bingo already exists in world '" + sessionName + "'!");
            return false;
        }

        BingoSession session = new BingoSession(this, menuBoard, WorldData.getOrCreateWorldGroup(plugin, sessionName), config, playerData);
        sessions.put(sessionName, session);
        return true;
    }

    public boolean destroySession(String sessionName) {
        if (singular)
        {
            Message.error("This command is not available when using configuration singular!");
            return false;
        }

        if (!sessions.containsKey(sessionName)) {
            return false;
        }

        endGame(sessionName);
        WorldData.destroyWorldGroup(plugin, WorldData.getOrCreateWorldGroup(plugin, sessionName));
        sessions.remove(sessionName);
        return true;
    }

    public boolean startGame(String sessionName) {
        if (!sessions.containsKey(sessionName)) {
            Message.log("Cannot start a game that doesn't exist, create it first using '/autobingo <world> create'!");
            return false;
        }

        if (isSessionRunning(sessionName)) {
            Message.log("Could not start bingo because the game is already running on world '" + sessionName + "'!");
            return false;
        }

        sessions.get(sessionName).startGame();
        return true;
    }

    public boolean endGame(String sessionName) {
        if (!isSessionRunning(sessionName)) {
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

    public void onPluginDisable() {
        HandlerList.unregisterAll(eventListener);
    }

    public ConfigData getGameConfig() {
        return config;
    }

    public boolean isSessionRunning(String sessionName) {
        return sessions.containsKey(sessionName) && sessions.get(sessionName).isRunning();
    }

    public boolean canPlayerOpenMenu(Player player, Menu menu) {
        return getSessionFromWorld(player.getWorld()) != null;
    }

    public boolean isConfigurationSingular()
    {
        return singular;
    }

    public Collection<String> getSessionNames() {
        return sessions.keySet();
    }
}
