package io.github.steaf23.bingoreloaded.item;

import org.bukkit.Material;

public class StatisticCardSlot extends AbstractCardSlot
{
    public StatisticCardSlot(Material material)
    {
        super(material);
    }

    @Override
    public AbstractCardSlot copy()
    {
        return new StatisticCardSlot(item.getType());
    }
}
