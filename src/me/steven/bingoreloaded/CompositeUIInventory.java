package me.steven.bingoreloaded;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class CompositeUIInventory extends AbstractGUIInventory
{
    public final AbstractGUIInventory inventory0;
    public final AbstractGUIInventory inventory1;

    private CompositeUIInventory(AbstractGUIInventory inventory0, AbstractGUIInventory inventory1, String title)
    {
        super(inventory0.inventory.getSize(), title);
        this.inventory0 = inventory0;
        this.inventory1 = inventory1;
    }

    public static CompositeUIInventory create(AbstractGUIInventory inv1, AbstractGUIInventory inv2, String title)
    {
        if (inv1.inventory.getSize() == inv2.inventory.getSize())
        {
            return new CompositeUIInventory(inv1, inv2, title);
        }

        return null;
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
