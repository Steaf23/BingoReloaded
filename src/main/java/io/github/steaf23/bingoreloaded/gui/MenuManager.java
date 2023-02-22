package io.github.steaf23.bingoreloaded.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

public class MenuManager
{
    private static List<MenuInventory> inventories;

    private static MenuManager INSTANCE;

    public static void create()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new MenuManager();
        }
    }

    public static void addInventory(MenuInventory inventory)
    {
        inventories.add(inventory);
    }

    private MenuManager()
    {
        inventories = new ArrayList<>();
    }

    public static void onInventoryClick(final InventoryClickEvent event)
    {
        for (var inv : inventories)
        {
            if (inv.inventory.equals(event.getInventory()))
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
            if (inv.inventory.equals(event.getInventory()))
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
            if (inv.inventory.equals(event.getInventory()))
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
            if (inv.inventory.equals(event.getInventory()))
            {
                inv.handleOpen(event);
                break;
            }
        }
    }
}
