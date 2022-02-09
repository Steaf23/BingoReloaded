package me.steven.bingoreloaded;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class BingoOptionsUI extends AbstractGUIInventory
{
    private BingoGame game;

    public BingoOptionsUI(BingoGame game)
    {
        super(54, BingoReloaded.PRINT_PREFIX + ChatColor.DARK_RED + " Options Menu");
        this.game = game;
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {

    }

    @Override
    public void delegateDrag(InventoryDragEvent event)
    {

    }
}
