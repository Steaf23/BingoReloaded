package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract class for Inventory based GUIs.
 * Delegates click events to a separate method that Inheritors must override.
 */
public abstract class MenuInventory
{
    /**
     * Event delegate to handle custom click behaviour for inventory screens.
     * @param event The event that gets fired when an item in the inventory was clicked.
     * @param slotClicked The slot that was clicked on by the player.
     * @param player The player that clicked on the item.
     */
    public abstract void delegateClick(final InventoryClickEvent event, int slotClicked, Player player, ClickType clickType);

    protected static final String TITLE_PREFIX = "" + ChatColor.GOLD + ChatColor.BOLD;
    protected final Inventory inventory;
    private final MenuInventory parent;
    private int maxStackSizeOverride = -1; // -1 means no override (i.e. default stack sizes for all items)

    public MenuInventory(int size, String title, MenuInventory parent)
    {
        this.parent = parent;
        this.inventory = Bukkit.createInventory(null, size, Message.PREFIX_STRING_SHORT + " " + ChatColor.DARK_RED + title);
        MenuEventListener.addInventory(this);
    }

    public MenuInventory(InventoryType type, String title, MenuInventory parent)
    {
        this.parent = parent;
        this.inventory = Bukkit.createInventory(null, type, Message.PREFIX_STRING_SHORT + " " + ChatColor.DARK_RED + title);
        MenuEventListener.addInventory(this);
    }

    protected void setMaxStackSizeOverride(int maxValue)
    {
        maxStackSizeOverride = Math.min(64, Math.max(1, maxValue));
    }

    public final void handleClick(final InventoryClickEvent event)
    {
        // ignore double clicks as they are annoying AF
        if (event.getClick() == ClickType.DOUBLE_CLICK)
            return;

        if (inventory == null) return;
        if (event.getInventory() == inventory)
        {
            event.setCancelled(true);

            if (event.getRawSlot() < event.getInventory().getSize() && event.getRawSlot() >= 0)
            {
                delegateClick(event, event.getSlot(), (Player)event.getWhoClicked(), event.getClick());
            }
        }
    }

    public void handleDrag(final InventoryDragEvent event)
    {
        if (inventory == null) return;
        if (event.getInventory() == inventory)
        {
            event.setCancelled(true);
        }
    }

    public void handleOpen(final InventoryOpenEvent event)
    {

    }

    public void handleClose(final InventoryCloseEvent event)
    {

    }

    protected void fillOptions(InventoryItem... options)
    {
        for (InventoryItem option : options)
        {
            addOption(option);
        }
    }

    /**
     * Adds items into the specified slot. If slot is -1, it will use the next available slot or merge using Inventory#addItem().
     * @param option menu item to put in the inventory
     */
    protected void addOption(InventoryItem option)
    {
        if (maxStackSizeOverride != -1)
            inventory.setMaxStackSize(maxStackSizeOverride);

        if (option.getSlot() == -1)
        {
            inventory.addItem(option);
        }
        else
        {
            inventory.setItem(option.getSlot(), option);
        }
    }

    public void clear()
    {
        inventory.clear();
    }

    public InventoryItem getOption(int slot)
    {
        ItemStack stack = inventory.getItem(slot);
        if (stack == null || stack.getType().isAir()) return null;

        return new InventoryItem(slot, inventory.getItem(slot));
    }

    public final void open(HumanEntity player)
    {
        Bukkit.getScheduler().runTask(BingoReloaded.get(), task -> {
            player.openInventory(inventory);
        });
    }

    public final void close(HumanEntity player)
    {
        if (parent != null)
            parent.open(player);
        else
            Bukkit.getScheduler().runTask(BingoReloaded.get(), task -> {
                player.closeInventory();
            });
    }

    public Inventory internalInventory()
    {
        return inventory;
    }
}
