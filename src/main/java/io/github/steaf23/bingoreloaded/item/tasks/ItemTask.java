package io.github.steaf23.bingoreloaded.item.tasks;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemTask extends AbstractBingoTask
{
    public ItemTask(Material material)
    {
        this(material, 1);
    }

    public ItemTask(Material material, int count)
    {
        super(material, ChatColor.YELLOW);
        item.setAmount(count);
        updateItem();
    }

    @Override
    public AbstractBingoTask copy()
    {
        ItemTask copy = new ItemTask(item.getType(), getCount());
        return copy;
    }

    @Override
    public String getKey()
    {
        return item.getType().name();
    }

    @Override
    public String getDisplayName()
    {
        if (getCount() > 1 && isComplete())
            return convertToReadableName(item.getType()) + " (" + getCount() + "x)";
        else
            return convertToReadableName(item.getType());
    }

    @Override
    public List<String> getDescription()
    {
        return List.of("Get " + getCount() + " of this item to complete this task!");
    }

    public int getCount()
    {
        return item.getAmount();
    }

    public static String convertToReadableName(@NotNull Material m)
    {
        String[] nameParts = m.name().split("_");
        String name = "";
        for (String section : nameParts)
        {
            name += capitalize(section) + " ";
        }
        return name.trim();
    }

    private static String capitalize(String str)
    {
        str = str.toLowerCase();
        if(str == null || str.length()<=1) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
