package io.github.steaf23.bingoreloaded.item.tasks;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.w3c.dom.Text;

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
    public BaseComponent getDisplayName()
    {
        return new TextComponent("dummy stats!");
    }

    @Override
    public void updateItemNBT()
    {
    }

    @Override
    public List<String> getDescription()
    {
        return List.of("Complete the statistic to complete this task!");
    }
}
