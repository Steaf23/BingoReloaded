package me.steven.bingoreloaded;

import org.bukkit.entity.Player;

public abstract class SubGUIInventory extends AbstractGUIInventory
{
    private final AbstractGUIInventory parent;

    public SubGUIInventory(int size, String title, AbstractGUIInventory parent)
    {
        super(size, title);
        this.parent = parent;
    }

    public void openParent(Player player)
    {
        if (parent != null)
            player.openInventory(parent.inventory);
    }
}
