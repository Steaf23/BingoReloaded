package io.github.steaf23.bingoreloaded.gui.base;


import io.github.steaf23.bingoreloaded.BingoReloaded;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class MenuManager implements Listener
{
    // Stores all currently open inventories by all players, using a stack system we can easily add or remove child inventories.
    Map<UUID, Stack<Menu>> activeMenus;

    public MenuManager() {
        this.activeMenus = new HashMap<>();
    }

    public void close(Menu menu, HumanEntity player) {
        UUID playerId = player.getUniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        // Return early if it's not on top of the menu stack (anymore).
        // This also guards against infinite closing loops regarding the closeEvent
        Stack<Menu> menus = activeMenus.get(playerId);
        if (menus.peek() != menu) {
            return;
        }

        Menu menuToClose = menus.pop();
        menuToClose.beforeClosing(player);
        if (menus.size() == 0) {
            activeMenus.remove(playerId);
            BingoReloaded.scheduleTask((task) -> menuToClose.closeInventory(player));
        } else {
            open(activeMenus.get(playerId).peek(), player);
        }
    }

    public void closeAll(HumanEntity player) {
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

    public void open(Menu menu, HumanEntity player) {
        UUID playerId = player.getUniqueId();
        if (!activeMenus.containsKey(playerId))
            activeMenus.put(playerId, new Stack<>());

        Stack<Menu> menuStack = activeMenus.get(playerId);
        // If we add another menu on top of a menu that should be removed, remove this menu first.
        if (menuStack.size() > 0 && menuStack.peek().openOnce()) {
            menuStack.pop().beforeClosing(player);
        }
        // If the new menu is not already in the stack, push it to the top.
        if (!menuStack.contains(menu)) {
            menuStack.push(menu);
        }
        // This menu is somewhere in the middle of the menu stack, don't open it.
        if (menuStack.peek() != menu) {
            return;
        }

        menu.beforeOpening(player);
        BingoReloaded.scheduleTask((task) -> menu.openInventory(player));
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
                event.getWhoClicked(),
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
        event.setCancelled(cancel);
    }

    @EventHandler
    public void handleInventoryClose(final InventoryCloseEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        Menu topMenu = activeMenus.get(playerId).peek();
        if (topMenu.getInventory() == event.getInventory()) {
            close(topMenu, event.getPlayer());
        }
    }

    @EventHandler
    public void handlePlayerQuit(final PlayerQuitEvent event) {
        if (activeMenus.containsKey(event.getPlayer().getUniqueId())) {
            closeAll(event.getPlayer());
        }
    }
}
