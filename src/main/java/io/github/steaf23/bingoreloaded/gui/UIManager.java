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

public class UIManager
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
        inventories = new ArrayList<>();
    }

    public static void onInventoryClick(final InventoryClickEvent event)
    {
        for (var inv : inventories)
        {
            if (inv.equals(event.getInventory()))
            {
                inv.handleClick(event);
                break;
            }
        }
    }

    public static void onInventoryDrag(final InventoryDragEvent event)
    {
        for (var inv : inventories)
        {
            if (inv.equals(event.getInventory()))
            {
                inv.handleDrag(event);
                break;
            }
        }
    }

    public static void onInventoryClose(final InventoryCloseEvent event)
    {
        for (var inv : inventories)
        {
            if (inv.equals(event.getInventory()))
            {
                inv.handleClose(event);
                break;
            }
        }
    }

    public static void onInventoryOpen(final InventoryOpenEvent event)
    {
        for (var inv : inventories)
        {
            if (inv.equals(event.getInventory()))
            {
                inv.handleOpen(event);
                break;
            }
        }
    }
}
