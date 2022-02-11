package me.steven.bingoreloaded.GUIInventories;

import org.bukkit.entity.HumanEntity;

public abstract class SubGUIInventory extends AbstractGUIInventory
{
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

    private final AbstractGUIInventory parent;
}
