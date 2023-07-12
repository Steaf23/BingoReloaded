package io.github.steaf23.bingoreloaded.gameloop.multiple;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.PlayerData;
import io.github.steaf23.bingoreloaded.event.BingoEventListener;
import io.github.steaf23.bingoreloaded.gameloop.BingoGameManager;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MultiGameManager implements BingoGameManager
{
    private final BingoEventListener eventListener;
    private Map<String, BingoSession> sessions;
    private final ConfigData config;
    private final PlayerData playerData;

    public MultiGameManager(BingoReloaded plugin) {
        this.config = plugin.config();
        this.eventListener = new BingoEventListener(world -> getSession(BingoReloaded.getWorldNameOfDimension(world)), config.disableAdvancements, config.disableStatistics);
        this.sessions = new HashMap<>();
        this.playerData = new PlayerData();

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

    public boolean createSession(String worldName, String presetName) {
        if (doesSessionExist(worldName)) {
            Message.log("An instance of Bingo already exists in world '" + worldName + "'!");
            return false;
        }

        BingoSession session = new BingoSession(getMenuManager(), worldName, config, playerData);
        sessions.put(worldName, session);
        return true;
    }

    public boolean destroySession(String worldName) {
        if (!doesSessionExist(worldName)) {
            return false;
        }

        endGame(worldName);
        sessions.remove(worldName);
        return true;
    }

    public boolean startGame(String worldName) {
        if (!doesSessionExist(worldName)) {
            Message.log("Cannot start a game that doesn't exist, create it first using '/autobingo <world> create'!");
            return false;
        }

        if (isGameWorldActive(worldName)) {
            Message.log("Could not start bingo because the game is already running on world '" + worldName + "'!");
            return false;
        }

        sessions.get(worldName).startGame();
        return true;
    }

    public boolean endGame(String worldName) {
        if (!isGameWorldActive(worldName)) {
            Message.log("Could not end bingo because no game was started on world '" + worldName + "'!");
            return false;
        }

        BingoSession session = sessions.get(worldName);
        session.endGame();
        return true;
    }

    public BingoSession getSession(String worldName) {
        if (sessions.containsKey(worldName)) {
            return sessions.get(worldName);
        }
        return null;
    }

    public boolean isGameWorldActive(String worldName) {
        return sessions.containsKey(worldName) && sessions.get(worldName).isRunning();
    }

    public boolean isGameWorldActive(World world) {
        return isGameWorldActive(BingoReloaded.getWorldNameOfDimension(world));
    }

    public boolean doesSessionExist(String worldName) {
        return sessions.containsKey(worldName);
    }

    public boolean doesSessionExist(World world) {
        return doesSessionExist(BingoReloaded.getWorldNameOfDimension(world));
    }
}
