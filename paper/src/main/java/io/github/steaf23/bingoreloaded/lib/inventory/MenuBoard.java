package io.github.steaf23.bingoreloaded.lib.inventory;


import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;
import io.github.steaf23.bingoreloaded.lib.events.PlayerDisplayAnvilTextChangedEvent;
import io.github.steaf23.bingoreloaded.lib.events.PlayerDisplayCustomClickActionEvent;
import io.github.steaf23.bingoreloaded.lib.util.NBTConverter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

public class MenuBoardPaper implements MenuBoard
{
    // Stores all currently open inventories by all players, using a stack system we can easily add or remove child inventories.
    protected final Map<UUID, Stack<Menu>> activeMenus;

    private final MenuPacketListener packetListener;

    private static final Set<ClickType> CLICK_TYPES_TO_IGNORE = Set.of(ClickType.DOUBLE_CLICK, ClickType.DROP, ClickType.CREATIVE, ClickType.CONTROL_DROP, ClickType.SWAP_OFFHAND);

    public MenuBoard() {
        this.activeMenus = new HashMap<>();
        this.packetListener = new MenuPacketListener();
    }

    public void close(Menu menu, PlayerHandle player) {
        UUID playerId = player.uniqueId();
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
        if (menus.isEmpty()) {
            activeMenus.remove(playerId);
            Bukkit.getScheduler().runTask(PlayerDisplay.getPlugin(), task -> player.closeInventory());
        } else {
            open(activeMenus.get(playerId).peek(), player);
        }
    }

    public void closeAll(PlayerHandle player) {
        UUID playerId = player.uniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        Stack<Menu> menus = activeMenus.get(playerId);
        while (!activeMenus.get(playerId).isEmpty()) {
            menus.pop().beforeClosing(player);
        }
        activeMenus.remove(playerId);
        player.closeInventory();
    }

    public void open(Menu menu, PlayerHandle player) {
        UUID playerId = player.uniqueId();
        if (!activeMenus.containsKey(playerId))
            activeMenus.put(playerId, new Stack<>());

        Stack<Menu> menuStack = activeMenus.get(playerId);
        // If we add another menu on top of a menu that should be removed, remove this menu first.
        if (!menuStack.isEmpty() && menuStack.peek().openOnce()) {
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

        if (menu instanceof InventoryMenu invMenu) {
            invMenu.openInventory(player);
        }
    }

    @EventHandler
    public void handleInventoryClick(final InventoryClickEvent event) {
        UUID playerId = event.getWhoClicked().getUniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        Menu menu = activeMenus.get(playerId).peek();
        if (!(menu instanceof InventoryMenu invMenu) || invMenu.getInventory() != event.getInventory()) {
            return;
        }

        event.setCancelled(true);

        // ignore click types that break everything
        if (CLICK_TYPES_TO_IGNORE.contains(event.getClick()))
            return;

        if (event.getInventory().getSize() < event.getRawSlot()
                || event.getRawSlot() < 0 || event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        boolean cancel = menu.onClick(event,
                event.getWhoClicked(),
                event.getRawSlot(),
                event.getClick());
        event.setCancelled(cancel);
    }

    @EventHandler
    public void handleInventoryDrag(final InventoryDragEvent event) {
        UUID playerId = event.getWhoClicked().getUniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        Menu menu = activeMenus.get(playerId).peek();
        if (!(menu instanceof InventoryMenu invMenu) || invMenu.getInventory() != event.getInventory()) {
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
        if (topMenu instanceof InventoryMenu invMenu && invMenu.getInventory() == event.getInventory()) {
            close(topMenu, event.getPlayer());
        }
    }

    @EventHandler
    public void handlePlayerQuit(final PlayerQuitEvent event) {
        if (activeMenus.containsKey(event.getPlayer().getUniqueId())) {
            closeAll(event.getPlayer());
        }
    }

    @EventHandler
    public void handlePlayerDisplayAnvilTextChanged(final PlayerDisplayAnvilTextChangedEvent event) {
        UUID playerId = event.getUserId();
        if (!activeMenus.containsKey(playerId))
            return;

        // There is no direct reference to the actual inventory in this event,
        // which is fine because a player can only open a single inventory at a time.
        // We just have to check the currently opened inventory for the given user uuid.
        Menu topMenu = activeMenus.get(playerId).peek();
        if (topMenu instanceof UserInputMenu inputMenu) {
            inputMenu.handleTextChanged(event.getNewText());
        }
    }

    @EventHandler
    public void handlePlayerDisplayCustomClickAction(final PlayerDisplayCustomClickActionEvent event) {
        UUID playerId = event.getUserId();
        if (!activeMenus.containsKey(playerId))
            return;

        Menu topMenu = activeMenus.get(playerId).peek();
        DataStorage payload = new TagDataStorage(NBTConverter.tagFromPacketEventsNBT(event.getPayload()));
        topMenu.onCustomAction(event.getActionKey(), payload);
    }
}