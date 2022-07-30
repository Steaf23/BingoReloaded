package io.github.steaf23.bingoreloaded.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class StatisticCardSlot extends AbstractCardSlot
{
    public StatisticCardSlot(Material material)
    {
        super(material, ChatColor.LIGHT_PURPLE);
    }

    @Override
    public AbstractCardSlot copy()
    {
        return new StatisticCardSlot(item.getType());
    }

    @Override
    public String getName()
    {
        return "stat-" + item.getType().name();
    }

    @Override
    public String getDisplayName()
    {
        return null;
    }
}
