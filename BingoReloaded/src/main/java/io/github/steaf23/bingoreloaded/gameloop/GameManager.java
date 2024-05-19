package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.PlayerSerializationData;
import io.github.steaf23.bingoreloaded.data.world.WorldData;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameManager
{
    protected final Map<String, BingoSession> sessions;

    private final JavaPlugin plugin;
    private final ConfigData config;
    private final MenuBoard menuBoard;

    private final PlayerSerializationData playerData;
    private final BingoEventListener eventListener;

    public GameManager(@NotNull JavaPlugin plugin, ConfigData config, MenuBoard menuBoard) {
        this.plugin = plugin;
        this.config = config;
        this.menuBoard = menuBoard;

        this.sessions = new HashMap<>();
        this.playerData = new PlayerSerializationData();
        this.eventListener = new BingoEventListener(this::getSessionFromWorld, config.disableAdvancements, config.disableStatistics);

        Bukkit.getPluginManager().registerEvents(eventListener, plugin);
    }

    public boolean createSession(String sessionName) {
        if (sessions.containsKey(sessionName)) {
            Message.log("An instance of Bingo already exists in world '" + sessionName + "'!");
            return false;
        }

        BingoSession session = new BingoSession(this, menuBoard, WorldData.createWorldGroup(plugin, sessionName), config, playerData);
        sessions.put(sessionName, session);
        return true;
    }

    public boolean destroySession(String sessionName) {
        if (!sessions.containsKey(sessionName)) {
            return false;
        }

        endGame(sessionName);
        WorldGroup group = WorldData.getWorldGroup(plugin, sessionName);
        if (group == null) {
            Message.error("Could not destroy worlds from session properly. (Please report!)");
            return false;
        }
        WorldData.destroyWorldGroup(plugin, WorldData.getWorldGroup(plugin, sessionName));
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

    public String getNameOfSession(@Nullable BingoSession session) {
        if (session == null) {
            return "";
        }

        return getSessionNames().stream().filter(name -> getSession(name) != null).findFirst().orElse("");
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

    public boolean teleportPlayerToSession(Player player, String sessionName) {
        WorldGroup bingoWorld = WorldData.getWorldGroup(plugin, sessionName);
        if (bingoWorld == null) {
            return false;
        }

        bingoWorld.teleportPlayer(player);
        return true;
    }

    public Collection<String> getSessionNames() {
        return sessions.keySet();
    }

    protected PlayerSerializationData getPlayerData() {
        return playerData;
    }
}
