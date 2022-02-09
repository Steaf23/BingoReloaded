package me.steven.bingoreloaded;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class BingoOptionsUI extends AbstractGUIInventory
{
    private BingoGame game;

    public BingoOptionsUI(BingoGame game)
    {
        this.game = game;
    }
    @Override
    public void showInventory(HumanEntity player)
    {

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
