package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.data.AdvancementData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;

public class AdvancementCardSlot extends AbstractCardSlot
{
    public Advancement advancement;
    public AdvancementCardSlot(Material material)
    {
        super(material, ChatColor.GREEN);
    }

    @Override
    public AbstractCardSlot copy()
    {
        AdvancementCardSlot copy = new AdvancementCardSlot(item.getType());
        copy.advancement = advancement;
        return copy;
    }

    @Override
    public String getName()
    {
        return advancement.getKey().toString();
    }

    @Override
    public String getDisplayName()
    {
        return AdvancementData.getAdvancementTitle(advancement.getKey().getKey());
    }
}
