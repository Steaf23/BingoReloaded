package io.github.steaf23.brmultimode;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.BingoReloadedExtension;
import io.github.steaf23.bingoreloaded.gui.base.MenuEventListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public final class BRMultiMode extends BingoReloadedExtension
{
    private BingoReloadedCore core;
    private BingoGameManager gameManager;
    private MenuEventListener menuManager;

    @Override
    public void onEnable()
    {
        this.gameManager = new BingoGameManager(core.config());
        this.menuManager = new MenuEventListener((view) -> {
            return gameManager.doesSessionExist(view.getPlayer().getWorld());
        });

        Bukkit.getPluginManager().registerEvents(gameManager.getListener(), this);
        Bukkit.getPluginManager().registerEvents(menuManager, this);
    }

    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll(gameManager.getListener());
        HandlerList.unregisterAll(menuManager);
    }
}
