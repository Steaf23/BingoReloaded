package io.github.steaf23.bingoreloaded.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ItemCardSlot extends AbstractCardSlot
{

    public ItemCardSlot(Material material)
    {
        super(material, ChatColor.YELLOW);
    }

    @Override
    public AbstractCardSlot copy()
    {
        ItemCardSlot copy = new ItemCardSlot(item.getType());
        copy.setCount(getCount());
        return copy;
    }

    @Override
    public String getName()
    {
        return item.getType().name();
    }

    @Override
    public String getDisplayName()
    {
        return convertToReadableName(item.getType());
    }

    public void setCount(int value)
    {
        item.setAmount(value);
    }

    public int getCount()
    {
        return item.getAmount();
    }

    public static String convertToReadableName(Material m)
    {
        String[] nameParts = m.name().split("_");
        String name = "";
        for (String section : nameParts)
        {
            name += capitalize(section) + " ";
        }
        return name;
    }

    private static String capitalize(String str)
    {
        if(str == null || str.length()<=1) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
