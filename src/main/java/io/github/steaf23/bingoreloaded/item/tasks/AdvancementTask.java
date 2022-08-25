package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.AdvancementData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public class AdvancementTask extends AbstractBingoTask
{
    public Advancement advancement;
    public AdvancementTask(Advancement advancement)
    {
        super(Material.FILLED_MAP, ChatColor.GREEN);
        this.advancement = advancement;
        updateItem();
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    }

    @Override
    public AbstractBingoTask copy()
    {
        AdvancementTask copy = new AdvancementTask(advancement);
        return copy;
    }

    @Override
    public String getKey()
    {
        return advancement.getKey().toString();
    }

    @Override
    public String getDisplayName()
    {
        if (advancement != null)
            return ChatColor.GREEN + "[" + AdvancementData.getAdvancementTitle(advancement) + "]" + ChatColor.RESET;
        return "";
    }

    @Override
    public List<String> getDescription()
    {
        return List.of(AdvancementData.getAdvancementDesc(advancement));
    }
}
