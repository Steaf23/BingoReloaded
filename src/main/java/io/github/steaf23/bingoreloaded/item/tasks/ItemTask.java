package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.ItemNameBuilder;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
    public BaseComponent getDisplayName()
    {
        BaseComponent comp = new TranslatableComponent(ItemNameBuilder.getTranslateKey(item.getType()));
        if (getCount() > 1 && isComplete())
        {
            comp.addExtra(" (" + getCount() + "x)");
            comp.setColor(ChatColor.GRAY);
            comp.setStrikethrough(true);
            return comp;
        }
        else
        {
            comp.setColor(nameColor);
            return comp;
        }
    }

    @Override
    public void updateItemNBT()
    {
        if (isComplete())
        {
            if (getCount() > 1)
            {
                new ItemNameBuilder(ChatColor.GRAY, false, false, true, false, false)
                        .translate(ItemNameBuilder.getTranslateKey(item.getType())).text( " (" + getCount() + "x)").build(item);
            }
            else
            {
                new ItemNameBuilder(ChatColor.GRAY, false, false, true, false, false)
                        .translate(ItemNameBuilder.getTranslateKey(item.getType())).build(item);
            }
        }
        else
        {
            new ItemNameBuilder(nameColor, false, false, false, false, false)
                    .translate(ItemNameBuilder.getTranslateKey(item.getType())).build(item);
        }
    }

    @Override
    public List<String> getDescription()
    {
        return Arrays.stream(TranslationData.translate("game.item.lore", "" + getCount()).split("\\n")).toList();
    }

    public static TranslatableComponent getTranslatedName(Material mat, Player player)
    {
        String result = "";
        TranslatableComponent name = new TranslatableComponent("item." + mat.name().toLowerCase() + ".name");

        return name;
    }

    public int getCount()
    {
        return item.getAmount();
    }

    private static String capitalize(String str)
    {
        str = str.toLowerCase();
        if(str == null || str.length()<=1) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
