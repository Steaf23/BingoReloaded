package io.github.steaf23.bingoreloaded.item;

import org.bukkit.Material;

public class ItemCardSlot extends AbstractCardSlot
{
    public int count = 1;

    public ItemCardSlot(Material material, int count)
    {
        this(material);
        this.count = count;
    }

    public ItemCardSlot(Material material)
    {
        super(material);
    }

    @Override
    public AbstractCardSlot copy()
    {
        return new ItemCardSlot(item.getType());
    }
}
