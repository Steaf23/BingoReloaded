package io.github.steaf23.bingoreloaded.gui.base2;


import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.*;
import java.util.List;

public class MenuManager implements Listener
{
    // Stores all currently open inventories by all players, using a stack system we can easily add or remove child inventories.
    Map<UUID, Stack<Menu>> activeMenus;

    public MenuManager() {
        this.activeMenus = new HashMap<>();
    }

    public void close(Menu menu, Player player) {
        UUID playerId = player.getUniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        // Return early if it's not on top of the menu stack (anymore).
        // This also guards against infinite closing loops regarding the closeEvent
        Stack<Menu> menus = activeMenus.get(playerId);
        if (menus.peek() != menu) {
            return;
        }

        menus.pop().beforeClosing(player);
        player.closeInventory();
    }

    public void closeAll(Player player) {
        UUID playerId = player.getUniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        Stack<Menu> menus = activeMenus.get(playerId);
        while (activeMenus.get(playerId).size() > 0) {
            menus.pop().beforeClosing(player);
        }
        activeMenus.remove(playerId);
        player.closeInventory();
    }

    public void open(Menu menu, Player player) {
        UUID playerId = player.getUniqueId();
        if (!activeMenus.containsKey(playerId))
            activeMenus.put(playerId, new Stack<>());

        activeMenus.get(playerId).push(menu);
        menu.beforeOpening(player);
        player.openInventory(menu.getInventory());
    }

    @EventHandler
    public void handleInventoryClick(final InventoryClickEvent event) {
        UUID playerId = event.getWhoClicked().getUniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        Menu menu = activeMenus.get(playerId).peek();
        if (menu.getInventory() != event.getInventory()) {
            return;
        }

        // ignore annoying double clicks..
        if (event.getClick() == ClickType.DOUBLE_CLICK)
            return;

        if (event.getInventory().getSize() < event.getRawSlot()
                || event.getRawSlot() < 0 || event.getCurrentItem() == null)
            return;

        boolean cancel = menu.onClick(event,
                (Player) event.getWhoClicked(),
                new MenuItem(event.getRawSlot(), event.getCurrentItem()),
                event.getClick());
        event.setCancelled(cancel);
    }

    @EventHandler
    public void handleInventoryDrag(final InventoryDragEvent event) {
        UUID playerId = event.getWhoClicked().getUniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        Menu menu = activeMenus.get(playerId).peek();
        if (menu.getInventory() != event.getInventory()) {
            return;
        }

        boolean cancel = menu.onDrag(event);
    }

    @EventHandler
    public void handleInventoryClose(final InventoryCloseEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        closeAll((Player) event.getPlayer());
    }
}
