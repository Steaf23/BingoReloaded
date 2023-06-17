package io.github.steaf23.bingoreloaded.gui.base2;

import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class Menu
{
    private Inventory inventory;
    private MenuManager manager;

    public Menu(MenuManager manager, String initialTitle, int rows) {
        this.manager = manager;
        this.inventory = Bukkit.createInventory(null, rows * 9, initialTitle);
    }

    public Menu(MenuManager manager, String initialTitle, InventoryType type) {
        this.manager = manager;
        this.inventory = Bukkit.createInventory(null, type, initialTitle);
    }

    public void open(Player player) {
        manager.open(this, player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void addItem(MenuItem item) {
        inventory.setItem(item.getSlot(), item);
    }

    public void addItems(MenuItem... items) {
        for (MenuItem item : items) {
            addItem(item);
        }
    }

    public void removeItem(int slotIdx) {

    }

    public void beforeOpening(Player player) {
        Message.log("BEFORE OPENING ME!");
    }

    /**
     * @param event
     * @param player
     * @param clickedItem
     * @param clickType
     * @return true if this event should be cancelled
     */
    public boolean onClick(final InventoryClickEvent event, Player player, MenuItem clickedItem, ClickType clickType) {
        return true;
    }

    /**
     * @param event
     * @return true if this even should be cancelled
     */
    public boolean onDrag(final InventoryDragEvent event) {
        return true;
    }

    public void beforeClosing(Player player) {
        Message.log("BEFORE CLOSING ME!");
    }
}
