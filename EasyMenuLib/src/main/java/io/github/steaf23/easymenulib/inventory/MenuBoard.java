package io.github.steaf23.easymenulib.inventory;


import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import io.github.steaf23.easymenulib.EasyMenuLibrary;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MenuBoard extends SimplePacketListenerAbstract implements Listener
{
    // Stores all currently open inventories by all players, using a stack system we can easily add or remove child inventories.
    protected Map<UUID, Stack<Menu>> activeMenus;

    private static final Set<ClickType> CLICK_TYPES_TO_IGNORE = Set.of(ClickType.DOUBLE_CLICK, ClickType.DROP, ClickType.CREATIVE, ClickType.CONTROL_DROP, ClickType.SWAP_OFFHAND);

    public MenuBoard() {
        this.activeMenus = new HashMap<>();
        PacketEvents.getAPI().getEventManager().registerListener(this);
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
        if (menus.isEmpty()) {
            activeMenus.remove(playerId);
            Bukkit.getScheduler().runTask(EasyMenuLibrary.getPlugin(), task -> player.closeInventory());
        } else {
            open(activeMenus.get(playerId).peek(), player);
        }
    }

    public void closeAll(HumanEntity player) {
        UUID playerId = player.getUniqueId();
        if (!activeMenus.containsKey(playerId))
            return;

        Stack<Menu> menus = activeMenus.get(playerId);
        while (!activeMenus.get(playerId).isEmpty()) {
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
        Bukkit.getScheduler().runTask(EasyMenuLibrary.getPlugin(), task -> player.openInventory(menu.getInventory()));
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

    //Packet events listener =========================================

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.NAME_ITEM) {
            WrapperPlayClientNameItem nameItem = new WrapperPlayClientNameItem(event);

            Stack<Menu> menus = activeMenus.get(event.getUser().getUUID());
            if (menus == null || menus.size() == 0) {
                return;
            }

            if (menus.peek() instanceof UserInputMenu inputMenu) {
                inputMenu.handleTextChanged(nameItem.getItemName());
            }

            event.setCancelled(true);
        }
    }
}
