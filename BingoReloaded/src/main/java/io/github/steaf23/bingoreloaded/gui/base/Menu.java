package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.gui.base.item.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public interface Menu
{
    void beforeOpening(HumanEntity player);

    /**
     * Implementations should return if the event should be cancelled.
     * @param event
     * @param player
     * @param clickedSlot
     * @param clickType
     * @return
     */
    boolean onClick(final InventoryClickEvent event, HumanEntity player, int clickedSlot, ClickType clickType);

    /**
     * Implementations should return if the event should be cancelled.
     * @param event
     * @return
     */
    boolean onDrag(final InventoryDragEvent event);

    void beforeClosing(HumanEntity player);

    default void openInventory(HumanEntity player) {
        player.openInventory(getInventory());
    }

    default void closeInventory(HumanEntity player) {
        player.closeInventory();
    }

    public MenuBoard getMenuBoard();

    /**
     * @return true if this menu should be removed if another menu opens on top of it.
     */
    default boolean openOnce() {
        return false;
    }

    Inventory getInventory();

    static String inputButtonText(String buttonText) {
        return ChatColor.DARK_GRAY + "<" + ChatColor.GRAY + buttonText + ChatColor.DARK_GRAY + ">" + ChatColor.WHITE + ": ";
    }
}
