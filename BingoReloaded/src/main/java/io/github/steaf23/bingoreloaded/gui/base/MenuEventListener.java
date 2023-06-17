package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Listens to inventory events and propagates them to the created MenuInventories if opened
 */
public class MenuEventListener implements Listener
{
    // Only react to events if precondition is true.
    private final Function<InventoryView, Boolean> menuPrecondition;
    private static List<MenuInventory> inventories;

    public static void addInventory(MenuInventory inventory)
    {
        inventories.add(inventory);
        Message.log("Amount of inventories: " + inventories.size());
    }

    public MenuEventListener(Function<InventoryView, Boolean> precondition)
    {
        inventories = new ArrayList<>();
        this.menuPrecondition = precondition;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        if (!menuPrecondition.apply(event.getView()))
        {
            return;
        }

        for (MenuInventory inventory : inventories)
        {
            if (inventory.internalInventory().equals(event.getInventory()))
            {
                inventory.handleClick(event);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event)
    {
        if (!menuPrecondition.apply(event.getView()))
        {
            return;
        }

        for (MenuInventory inventory : inventories)
        {
            if (inventory.internalInventory().equals(event.getInventory()))
            {
                inventory.handleDrag(event);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event)
    {
        if (!menuPrecondition.apply(event.getView()))
        {
            return;
        }

        for (MenuInventory inventory : inventories)
        {
            if (inventory.internalInventory().equals(event.getInventory()))
            {
                inventory.handleOpen(event);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event)
    {
        if (!menuPrecondition.apply(event.getView()))
        {
            return;
        }

        for (MenuInventory inventory : inventories)
        {
            if (inventory.internalInventory().equals(event.getInventory()))
            {
                inventory.handleClose(event);
                break;
            }
        }
    }
}
