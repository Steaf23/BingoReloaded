package io.github.steaf23.bingoreloaded.gameloop.singular;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.PlayerData;
import io.github.steaf23.bingoreloaded.event.BingoEventListener;
import io.github.steaf23.bingoreloaded.gameloop.BingoGameManager;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.base.BingoMenuManager;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class SingularGameManager implements BingoGameManager
{
    private final BingoEventListener eventListener;
    private final BingoSession session;
    private final BingoMenuManager menuManager;
    private final ConfigData config;

    public SingularGameManager(BingoReloaded plugin) {
        this.config = plugin.config();
        this.menuManager = new BingoMenuManager(player -> canOpenMenu(player));
        this.session = new BingoSession(menuManager, config.defaultWorldName, config, new PlayerData());

        this.eventListener = new BingoEventListener(world ->
                BingoReloaded.getWorldNameOfDimension(world).equals(session.worldName) ? session : null
                , config.disableAdvancements, config.disableStatistics);

        plugin.registerCommand("bingobot", new BotCommand(session.teamManager));

        Bukkit.getPluginManager().registerEvents(eventListener, plugin);
        Bukkit.getPluginManager().registerEvents(menuManager, plugin);
    }

    @Override
    public BingoSession getSession(String worldName) {
        return session;
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

    public boolean canOpenMenu(HumanEntity player) {
        if (session == null)
            return false;

        String worldName = BingoReloaded.getWorldNameOfDimension(player.getWorld());
        return worldName.equals(session.worldName);
    }
}
