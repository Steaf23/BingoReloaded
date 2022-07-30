package io.github.steaf23.bingoreloaded.item;

import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ItemCardSlot extends AbstractCardSlot
{
    public int count = 1;

    public ItemCardSlot(Material material)
    {
        super(material, ChatColor.YELLOW);
    }

    @Override
    public AbstractCardSlot copy()
    {
        ItemCardSlot copy = new ItemCardSlot(item.getType());
        copy.count = count;
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

    public static String convertToReadableName(Material m)
    {
        String name = m.name().replace("_", " ");
        return WordUtils.capitalizeFully(name);
    }
}
