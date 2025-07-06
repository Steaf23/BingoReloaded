package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;

import java.awt.*;
import java.util.function.Function;

public class BingoMenuBoard implements MenuBoard {
    private Function<PlayerHandle, Boolean> playerPredicate;

    public BingoMenuBoard()
    {
        super();
        this.playerPredicate = player -> false;
    }

    public void setPlayerOpenPredicate(Function<PlayerHandle, Boolean> playerPredicate) {
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
