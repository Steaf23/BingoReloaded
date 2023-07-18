package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BasicMenu implements Menu
{
    public static String pluginTitlePrefix = "";

    private static int ID_COUNTER = 0;

    protected static final String TITLE_PREFIX = "" + ChatColor.GOLD + ChatColor.BOLD;

    protected static MenuItem BLANK = new MenuItem(Material.BLACK_STAINED_GLASS_PANE, " ");

    private final Inventory inventory;
    private final MenuManager manager;
    private int maxStackSizeOverride = -1; // -1 means no override (i.e. default stack sizes for all items)

    private final Map<String, Consumer<HumanEntity>> actions;

    public BasicMenu(MenuManager manager, String initialTitle, int rows) {
        this(manager, Bukkit.createInventory(null, rows * 9, pluginTitlePrefix + initialTitle));
    }

    public BasicMenu(MenuManager manager, String initialTitle, InventoryType type) {
        this(manager, Bukkit.createInventory(null, type, pluginTitlePrefix + initialTitle));
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

    public BasicMenu addItem(@NotNull MenuItem item) {
        if (maxStackSizeOverride != -1)
            inventory.setMaxStackSize(maxStackSizeOverride);

        if (item.getSlot() == -1) {
            inventory.addItem(item);
        } else {
            ItemStack existingItem = inventory.getItem(item.getSlot());
            if (existingItem != null) {
                // Remove any actions on the existing space
                String actionId = new MenuItem(existingItem).getStringFromPdc("action");
                actions.remove(actionId);
            }
            // Replace/ set new item in its target slot
            inventory.setItem(item.getSlot(), item);
        }

        return this;
    }

    public BasicMenu addAction(@NotNull MenuItem item, Consumer<HumanEntity> action) {
        String actionId = "" + ID_COUNTER;
        ID_COUNTER++;

        item.addStringToPdc("action", actionId);
        addItem(item);

        // Add actions after we add the item, since otherwise it will be removed
        actions.put(actionId, action);
        return this;
    }

    public BasicMenu updateActionItem(@NotNull MenuItem newItem) {
        ItemStack oldItem = inventory.getItem(newItem.getSlot());
        if (oldItem == null) {
            Message.log("Could not update action item, no action was placed on this slot!");
            return this;
        }

        String actionId = new MenuItem(oldItem).getStringFromPdc("action");

        if (actionId.isEmpty()) {
            Message.log("Could not update action item, no action was placed on this slot!");
            return this;
        }

        Consumer<HumanEntity> action = actions.get(actionId);
        actions.remove(actionId);
        addAction(newItem, action);
        return this;
    }

    public BasicMenu addCloseAction(@NotNull MenuItem item) {
        return addAction(item, this::close);
    }

    public BasicMenu addItems(@NotNull MenuItem... items) {
        for (MenuItem item : items) {
            addItem(item);
        }
        return this;
    }

    public MenuItem getItemAt(int slotIndex) {
        return new MenuItem(slotIndex, getInventory().getItem(slotIndex));
    }

    public BasicMenu removeItem(int slotIdx) {
        inventory.setItem(slotIdx, null);
        return this;
    }

    protected void setMaxStackSizeOverride(int maxValue) {
        maxStackSizeOverride = Math.min(64, Math.max(1, maxValue));
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public MenuManager getMenuManager() {
        return this.manager;
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
