package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

public class UIManager implements Listener
{
    private static List<AbstractGUIInventory> inventories;

    private static UIManager INSTANCE;

    public static void create()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new UIManager();
        }
    }

    public static void addInventory(AbstractGUIInventory inventory)
    {
        inventories.add(inventory);
    }

    private UIManager()
    {
        BingoReloaded.registerListener(this);
        inventories = new ArrayList<>();
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        for (var inv : inventories)
        {
            if (inv.getHolder().equals(event.getInventory().getHolder()))
            {
                inv.handleClick(event);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event)
    {
        for (var inv : inventories)
        {
            if (inv.getHolder().equals(event.getInventory().getHolder()))
            {
                inv.handleDrag(event);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event)
    {
        for (var inv : inventories)
        {
            if (inv.getHolder().equals(event.getInventory().getHolder()))
            {
                inv.handleClose(event);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event)
    {
        for (var inv : inventories)
        {
            if (inv.getHolder().equals(event.getInventory().getHolder()))
            {
                inv.handleOpen(event);
                break;
            }
        }
    }
}
