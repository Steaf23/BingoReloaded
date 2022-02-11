package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class AbstractGUIInventory implements Listener
{
    protected static final String TITLE_PREFIX = "" + ChatColor.GOLD + ChatColor.BOLD;

    private Inventory inventory = null;
    //used to process logic when player clicks on an item in the inventory.
    public abstract void delegateClick(InventoryClickEvent event);
    //used to process logic when player drags an item in the inventory.
    public abstract void delegateDrag(InventoryDragEvent event);

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
                delegateClick(event);
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

            delegateDrag(event);
        }
    }

    protected void fillOptions(int[] slots, MenuItem[] options)
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
    protected void addOption(int slot, MenuItem option)
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

    public MenuItem getOption(int slot)
    {
        ItemStack stack = inventory.getItem(slot);
        if (stack == null) return null;

        return new MenuItem(inventory.getItem(slot));
    }

    public void open(HumanEntity player)
    {
        player.openInventory(inventory);
    }

    /**
     * Compares the given ItemStack getDisplayName to the MenuItem DisplayName and returns true if they are equal.
     */
    protected static boolean isMenuItem(ItemStack stack, MenuItem menuItem)
    {
        return stack.getItemMeta().getDisplayName().equals(menuItem.getItemMeta().getDisplayName());
    }
}
