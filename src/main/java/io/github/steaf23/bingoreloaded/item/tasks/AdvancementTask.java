package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.AdvancementData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public class AdvancementTask extends AbstractBingoTask
{
    public Advancement advancement;
    public AdvancementTask(Advancement advancement)
    {
        super(Material.FILLED_MAP, ChatColor.AQUA);
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
            return "[" + AdvancementData.getAdvancementTitle(advancement) + "]";
        return "";
    }

    @Override
    public List<String> getDescription()
    {
        return advancement == null ? List.of("") : List.of(AdvancementData.getAdvancementDesc(advancement));
    }
}
