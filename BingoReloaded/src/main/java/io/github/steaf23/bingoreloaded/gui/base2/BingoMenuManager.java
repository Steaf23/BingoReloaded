package io.github.steaf23.bingoreloaded.gui.base2;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;

public class BingoMenuManager extends MenuManager
{
    private Function<Player, Boolean> playerPredicate;

    public BingoMenuManager(Function<Player, Boolean> playerPredicate)
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
    public void open(Menu menu, Player player) {
        if (!playerPredicate.apply(player))
            return;

        super.open(menu, player);
    }

    @Override
    public void close(Menu menu, Player player) {
        if (!playerPredicate.apply(player))
            return;

        super.close(menu, player);
    }

    @Override
    public void closeAll(Player player) {
        if (!playerPredicate.apply(player))
            return;

        super.closeAll(player);
    }
}
