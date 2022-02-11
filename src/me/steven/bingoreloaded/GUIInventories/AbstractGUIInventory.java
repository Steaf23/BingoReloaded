package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Abstract class for Inventory based GUIs.
 * Delegates click events to a separate method that Inheritors must override.
 */
public abstract class AbstractGUIInventory implements Listener
{
    protected static final String TITLE_PREFIX = "" + ChatColor.GOLD + ChatColor.BOLD;

    /**
     * Event delegate to handle custom click behaviour for inventory items
     * @param event The event that gets fired when an item in the inventory was clicked.
     * @param itemClicked The item that was clicked on by the player.
     *                    Item can be null.
     * @param player The player that clicked on the item.
     */
    public abstract void delegateClick(InventoryClickEvent event, ItemStack itemClicked, Player player);

    public AbstractGUIInventory(int size, String title)
    {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        if (plugin == null) return;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        inventory = Bukkit.createInventory(null, size, BingoReloaded.PRINT_PREFIX + ChatColor.DARK_RED + title);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        if (inventory == null) return;
        if (event.getInventory() == inventory)
        {
            event.setCancelled(true);

            if (event.getRawSlot() < event.getInventory().getSize())
            {
                delegateClick(event, event.getCurrentItem(), (Player)event.getWhoClicked());
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event)
    {
        if (inventory == null) return;
        if (event.getInventory() == inventory)
        {
            event.setCancelled(true);
        }
    }

    protected void fillOptions(int[] slots, CustomItem[] options)
    {
        if (slots.length != options.length) throw new IllegalArgumentException("Number of options and number of slots to put them in are not equal!");
        if (inventory.getSize() < slots.length) throw new IllegalArgumentException("Cannot fill options with current inventory size (" + inventory.getSize() + ")!");

        for (int i = 0; i < slots.length; i++)
        {
            addOption(slots[i], options[i]);
        }
    }

    /**
     * Adds items into the specified slot. If slot is -1, it will use the next available slot or merge using Inventory#addItem().
     * @param slot inventory slot to fill
     * @param option menu item to put in the inventory
     */
    protected void addOption(int slot, CustomItem option)
    {
        if (slot == -1)
        {
            inventory.addItem(option);
        }
        else
        {
            inventory.setItem(slot, option);
        }
    }

    public CustomItem getOption(int slot)
    {
        ItemStack stack = inventory.getItem(slot);
        if (stack == null) return null;

        return new CustomItem(inventory.getItem(slot));
    }

    public void open(HumanEntity player)
    {
        player.openInventory(inventory);
    }

    /**
     * Compares the given ItemStack getDisplayName to the MenuItem DisplayName and returns true if they are equal.
     */
    protected static boolean isMenuItem(ItemStack stack, CustomItem customItem)
    {
        return stack.getItemMeta().getDisplayName().equals(customItem.getItemMeta().getDisplayName());
    }

    private Inventory inventory = null;
}
