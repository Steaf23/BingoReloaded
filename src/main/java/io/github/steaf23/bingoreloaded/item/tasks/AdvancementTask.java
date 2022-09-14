package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.AdvancementData;
import io.github.steaf23.bingoreloaded.item.ItemNameBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.w3c.dom.Text;

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
    public BaseComponent getDisplayName()
    {
        if (advancement == null)
            return new TextComponent("NO ADVANCEMENT");

        BaseComponent base = new TextComponent("[" + AdvancementData.getAdvancementTitle(advancement) + "]");
        if (isComplete())
        {
            base.setStrikethrough(true);
            base.setColor(ChatColor.GRAY);
        }
        else
        {
            base.setColor(nameColor);
        }

        return base;
    }

    @Override
    public void updateItemNBT()
    {
        if (advancement == null)
            new ItemNameBuilder(ChatColor.DARK_RED, false, false, false, false, false)
                    .text("NO ADVANCEMENT").build(item);
        if (isComplete())
        {
            new ItemNameBuilder(ChatColor.GRAY, false, false, true, false, false)
                    .text("[" + AdvancementData.getAdvancementTitle(advancement) + "]")
                    .build(item);
        }
        else
        {
            new ItemNameBuilder(nameColor, false, false, false, false, false)
                    .text("[" + AdvancementData.getAdvancementTitle(advancement) + "]")
                    .build(item);
        }
    }

    @Override
    public List<String> getDescription()
    {
        return advancement == null ? List.of("") : List.of(AdvancementData.getAdvancementDesc(advancement));
    }
}
