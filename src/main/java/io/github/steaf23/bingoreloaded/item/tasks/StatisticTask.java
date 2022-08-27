package io.github.steaf23.bingoreloaded.item.tasks;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

public class StatisticTask extends AbstractBingoTask
{
    public StatisticTask(Material material)
    {
        super(material, ChatColor.LIGHT_PURPLE);
        updateItem();
    }

    @Override
    public AbstractBingoTask copy()
    {
        return new StatisticTask(item.getType());
    }

    @Override
    public String getKey()
    {
        return "stat-" + item.getType().name();
    }

    @Override
    public String getDisplayName()
    {
        return null;
    }

    @Override
    public List<String> getDescription()
    {
        return List.of("Complete the statistic to complete this task!");
    }
}
