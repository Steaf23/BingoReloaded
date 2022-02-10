package me.steven.bingoreloaded.GUIInventories;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class DifficultyOptionsUI extends SubGUIInventory
{
    public DifficultyOptionsUI(AbstractGUIInventory parent)
    {
        super(9, "Choose Difficulty", parent);
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {
        openParent((Player)event.getWhoClicked());
    }

    @Override
    public void delegateDrag(InventoryDragEvent event)
    {

    }
}
