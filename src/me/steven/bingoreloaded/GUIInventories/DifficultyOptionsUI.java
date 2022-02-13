package me.steven.bingoreloaded.GUIInventories;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DifficultyOptionsUI extends AbstractGUIInventory
{
    public DifficultyOptionsUI(AbstractGUIInventory parent)
    {
        super(9, "Choose Difficulty", parent);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        openParent(player);
    }
}
