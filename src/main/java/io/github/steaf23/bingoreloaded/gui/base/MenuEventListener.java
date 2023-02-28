package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.BingoGameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Listens to inventory events and propagates them to the created MenuInventories if opened
 */
public class MenuEventListener implements Listener
{
    private static List<MenuInventory> inventories;

    private static MenuEventListener INSTANCE;

    public static MenuEventListener get()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new MenuEventListener();
        }
        return INSTANCE;
    }

    public static void addInventory(MenuInventory inventory)
    {
        inventories.add(inventory);
    }

    private MenuEventListener()
    {
        inventories = new ArrayList<>();
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        String worldName = BingoGameManager.getWorldName(event.getWhoClicked().getWorld());
        if (!BingoGameManager.get().doesGameWorldExist(worldName))
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
        String worldName = BingoGameManager.getWorldName(event.getWhoClicked().getWorld());
        if (!BingoGameManager.get().doesGameWorldExist(worldName))
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
        String worldName = BingoGameManager.getWorldName(event.getPlayer().getWorld());
        if (!BingoGameManager.get().doesGameWorldExist(worldName))
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
        String worldName = BingoGameManager.getWorldName(event.getPlayer().getWorld());
        if (!BingoGameManager.get().doesGameWorldExist(worldName))
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
