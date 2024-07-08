package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.playerdisplay.inventory.Menu;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;

public class BingoMenuBoard extends MenuBoard
{
    private Function<HumanEntity, Boolean> playerPredicate;

    public BingoMenuBoard()
    {
        super();
        this.playerPredicate = player -> false;
    }

    public void setPlayerOpenPredicate(Function<HumanEntity, Boolean> playerPredicate) {
        this.playerPredicate = playerPredicate;
    }

    @Override
    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        if (!playerPredicate.apply(event.getWhoClicked()))
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
