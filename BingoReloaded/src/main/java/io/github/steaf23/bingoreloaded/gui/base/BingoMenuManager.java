package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;

public class BingoMenuManager extends MenuManager
{
    private Function<HumanEntity, Boolean> playerPredicate;

    public BingoMenuManager(Function<HumanEntity, Boolean> playerPredicate)
    {
        this.playerPredicate = playerPredicate;
    }

    @Override
    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        if (!playerPredicate.apply((Player)event.getWhoClicked()))
            return;

        super.handleInventoryClick(event);
    }

    @Override
    public void open(Menu menu, HumanEntity player) {
        if (!playerPredicate.apply(player))
            return;

        super.open(menu, player);
    }

    @Override
    public void close(Menu menu, HumanEntity player) {
        if (!playerPredicate.apply(player))
            return;

        super.close(menu, player);
    }

    @Override
    public void closeAll(HumanEntity player) {
        if (!playerPredicate.apply(player))
            return;

        super.closeAll(player);
    }

    @EventHandler
    public void handlePlayerLeft(final PlayerLeftSessionWorldEvent event) {
        if (activeMenus.containsKey(event.getPlayer().getUniqueId())) {
            closeAll(event.getPlayer());
        }
    }
}
