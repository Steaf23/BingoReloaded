package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.ItemTextBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

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
        BaseComponent comp = new TranslatableComponent(ItemTextBuilder.getItemKey(item.getType()));
        if (getCount() > 1 && isComplete())
        {
            comp.addExtra(" (" + getCount() + "x)");
            comp.setColor(ChatColor.GRAY);
            comp.setStrikethrough(true);
        }
        else
        {
            comp.setColor(nameColor);
        }
        return comp;
    }

    @Override
    public BaseComponent getDescription()
    {
        BaseComponent base = new TextComponent();
        for (var line : getItemLore())
        {
            base.addExtra(line);
        }
        return base;
    }

    @Override
    public List<String> getItemLore()
    {
        return Arrays.stream(TranslationData.translate("game.item.lore", "" + getCount()).split("\\n")).toList();
    }

    @Override
    public void updateItemName()
    {
        if (isComplete())
        {
            if (getCount() > 1)
            {
                new ItemTextBuilder(ChatColor.GRAY, "strikethrough")
                        .translate(ItemTextBuilder.getItemKey(item.getType())).text( " (" + getCount() + "x)").buildName(item);
            }
            else
            {
                new ItemTextBuilder(ChatColor.GRAY, "strikethrough")
                        .translate(ItemTextBuilder.getItemKey(item.getType())).buildName(item);
            }
        }
        else
        {
            new ItemTextBuilder(nameColor)
                    .translate(ItemTextBuilder.getItemKey(item.getType())).buildName(item);
        }
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
