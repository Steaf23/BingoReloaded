package me.steven.bingoreloaded.GUIInventories;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public abstract class SubGUIInventory extends AbstractGUIInventory
{
    private final AbstractGUIInventory parent;

    public SubGUIInventory(int size, String title, AbstractGUIInventory parent)
    {
        super(size, title);
        this.parent = parent;
    }

    public void openParent(HumanEntity player)
    {
        if (parent != null)
            parent.open(player);
    }
}
