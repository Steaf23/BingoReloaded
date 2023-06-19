package io.github.steaf23.bingoreloaded.gui.base2;

import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BasicMenu implements Menu
{
    protected static final String TITLE_PREFIX = "" + ChatColor.GOLD + ChatColor.BOLD;

    private final Inventory inventory;
    private final MenuManager manager;
    private int maxStackSizeOverride = -1; // -1 means no override (i.e. default stack sizes for all items)

    private Map<String, Consumer<HumanEntity>> actions;

    public BasicMenu(MenuManager manager, String initialTitle, int rows) {
        this(manager, Bukkit.createInventory(null, rows * 9, initialTitle));
    }

    public BasicMenu(MenuManager manager, String initialTitle, InventoryType type) {
        this(manager, Bukkit.createInventory(null, type, initialTitle));
    }

    // Used for common setup
    private BasicMenu(MenuManager manager, Inventory inventory) {
        this.inventory = inventory;
        this.manager = manager;
        this.actions = new HashMap<>();
    }

    public void open(HumanEntity player) {
        manager.open(this, player);
    }

    public void close(HumanEntity player) {
        manager.close(this, player);
    }

    public void addItem(@NotNull MenuItem item) {
        if (maxStackSizeOverride != -1)
            inventory.setMaxStackSize(maxStackSizeOverride);

        if (item.getSlot() == -1) {
            inventory.addItem(item);
        } else {
            inventory.setItem(item.getSlot(), item);
        }
    }

    public void addAction(@NotNull MenuItem item, Consumer<HumanEntity> action) {
        String actionId = "action_" + actions.size();
        actions.put(actionId, action);

        item.addStringToPdc("action", actionId);
        addItem(item);
    }

    public void addCloseAction(@NotNull MenuItem item) {
        addAction(item, (player) -> close(player));
    }

    public void addItems(@NotNull MenuItem... items) {
        for (MenuItem item : items) {
            addItem(item);
        }
    }

    public MenuItem getItemAt(int slotIndex) {
        return new MenuItem(slotIndex, getInventory().getItem(slotIndex));
    }

    public void removeItem(int slotIdx) {
        inventory.setItem(slotIdx, null);
    }

    protected void setMaxStackSizeOverride(int maxValue) {
        maxStackSizeOverride = Math.min(64, Math.max(1, maxValue));
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void beforeOpening(HumanEntity player) {
    }

    /**
     * @param event
     * @param player
     * @param clickedItem
     * @param clickType
     * @return true if this event should be cancelled
     */
    @Override
    public boolean onClick(final InventoryClickEvent event, HumanEntity player, MenuItem clickedItem, ClickType clickType) {
        String actionId = clickedItem.getStringFromPdc("action");
        if (actions.containsKey(actionId)) {
            actions.get(actionId).accept(player);
        }

        return true;
    }

    /**
     * @param event
     * @return true if this even should be cancelled
     */
    @Override
    public boolean onDrag(final InventoryDragEvent event) {
        return true;
    }

    @Override
    public void beforeClosing(HumanEntity player) {
    }
}
