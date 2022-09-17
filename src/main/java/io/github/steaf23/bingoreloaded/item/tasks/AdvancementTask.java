package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.ItemTextBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.enchantments.Enchantment;

import java.util.Arrays;
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

        BaseComponent base = new TextComponent("[");
        base.addExtra(new TranslatableComponent(ItemTextBuilder.getAdvancementTitleKey(advancement)));
        if (isComplete())
        {
            base.setStrikethrough(true);
            base.setColor(ChatColor.GRAY);
        }
        else
        {
            base.setColor(nameColor);
        }
        base.addExtra("]");

        return base;
    }

    @Override
    public BaseComponent getDescription()
    {
        return new TranslatableComponent(ItemTextBuilder.getAdvancementDescKey(advancement));
    }

    @Override
    public List<String> getItemLore()
    {
        return Arrays.stream(TranslationData.translate("game.item.lore_advancement").split("\\n")).toList();
    }

    @Override
    public void updateItemName()
    {
        if (advancement == null)
            new ItemTextBuilder(ChatColor.DARK_RED)
                    .text("NO ADVANCEMENT").buildName(item);
        if (isComplete())
        {
            new ItemTextBuilder(ChatColor.GRAY, "strikethrough")
                    .text("[")
                    .translate(ItemTextBuilder.getAdvancementTitleKey(advancement))
                    .text("]")
                    .buildName(item);
        }
        else
        {
            new ItemTextBuilder(nameColor)
                    .text("[")
                    .translate(ItemTextBuilder.getAdvancementTitleKey(advancement))
                    .text("]")
                    .buildName(item);
        }
    }
}
