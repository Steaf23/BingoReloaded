package io.github.steaf23.bingoreloaded.gameloop.singular;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.PlayerSerializationData;
import io.github.steaf23.bingoreloaded.event.BingoEventListener;
import io.github.steaf23.bingoreloaded.gameloop.SessionManager;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.WorldGroup;
import io.github.steaf23.bingoreloaded.gui.base.BingoMenuManager;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class SingularGameManager implements SessionManager
{
    private final BingoEventListener eventListener;
    private final BingoSession session;
    private final BingoMenuManager menuManager;
    private final ConfigData config;

    private final String sessionName;

    public SingularGameManager(BingoReloaded plugin) {
        this.config = plugin.config();
        this.menuManager = new BingoMenuManager(player -> canOpenMenu(player));
        this.session = new BingoSession(this, menuManager, new WorldGroup(plugin.worldData, config.defaultWorldName), config, new PlayerSerializationData());
        this.sessionName = config.defaultWorldName;
        this.eventListener = new BingoEventListener(world ->
                session.ownsWorld(world) ? session : null
                , config.disableAdvancements, config.disableStatistics);

        plugin.registerCommand("bingobot", new BotCommand(session.teamManager));

        Bukkit.getPluginManager().registerEvents(eventListener, plugin);
        Bukkit.getPluginManager().registerEvents(menuManager, plugin);
    }

    @Override
    public BingoSession getSession(String sessionName) {
        if (sessionName.equals(this.sessionName)) {
            return session;
        }
        return null;
    }

    @Nullable
    @Override
    public BingoSession getSessionFromWorld(World world) {
        if (session.ownsWorld(world)) {
            return session;
        }
        return null;
    }

    public BingoSession getSession() {
        return session;
    }

    @Override
    public MenuManager getMenuManager() {
        return menuManager;
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
        return 1;
    }

    public boolean canOpenMenu(HumanEntity player) {
        if (session == null || !(player instanceof Player p))
            return false;

        return session.hasPlayer(p);
    }
}
