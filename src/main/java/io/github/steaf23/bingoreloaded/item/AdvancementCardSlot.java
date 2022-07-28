package io.github.steaf23.bingoreloaded.item;

import org.bukkit.Material;

public class AdvancementCardSlot extends AbstractCardSlot
{
    public AdvancementCardSlot(Material material)
    {
        super(material);
    }

    @Override
    public AbstractCardSlot copy()
    {
        return new AdvancementCardSlot(item.getType());
    }
}
