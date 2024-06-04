package io.github.steaf23.easymenulib.inventory;

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

    private static String inputButtonText(String buttonText) {
        return ChatColor.DARK_GRAY + "<" + ChatColor.GRAY + buttonText + ChatColor.DARK_GRAY + ">" + ChatColor.WHITE + ": ";
    }

    public static String INPUT_LEFT_CLICK = inputButtonText("Left Click");
    public static String INPUT_RIGHT_CLICK = inputButtonText("Right Click");
    public static String INPUT_SHIFT_CLICK = inputButtonText("Hold Shift");
}
