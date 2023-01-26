package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.ItemText;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record ItemTask(Material material, int count) implements TaskData
{
    public ItemTask(Material material)
    {
        this(material, 1);
    }

    @Override
    public ItemText getItemDisplayName()
    {
        ItemText text = new ItemText();
        text.addText(Integer.toString(count) + "x ");
        text.addItemName(material);
        return text;
    }

    @Override
    public ItemText[] getItemDescription()
    {
        Set<ChatColor> modifiers = new HashSet<>(){{
            add(ChatColor.DARK_AQUA);
        }};
        return TranslationData.translateToItemText("game.item.lore", modifiers, new ItemText(Integer.toString(count)));
    }

    @Override
    public BaseComponent getDescription()
    {
        return ItemText.combine(getItemDescription()).asComponent();
    }

    @Override
    public int getStackSize()
    {
        return count;
    }

    @Override
    public PersistentDataContainer pdcSerialize(PersistentDataContainer stream)
    {
        stream.set(BingoTask.getTaskDataKey("item"),  PersistentDataType.STRING, material.name());
        stream.set(BingoTask.getTaskDataKey("count"),  PersistentDataType.INTEGER, count);
        return stream;
    }

    public static ItemTask fromPdc(PersistentDataContainer pdc)
    {
        Material item = Material.valueOf(pdc.getOrDefault(BingoTask.getTaskDataKey("item"), PersistentDataType.STRING, "BEDROCK"));
        int count = pdc.getOrDefault(BingoTask.getTaskDataKey("count"), PersistentDataType.INTEGER, 1);
        ItemTask rec = new ItemTask(item, count);
        return rec;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize()
    {
        return new HashMap<>(){{
            put("item", material.name());
            put("count", count);
        }};
    }

    public static ItemTask deserialize(Map<String, Object> data)
    {
        return new ItemTask(
                Material.valueOf((String) data.get("item")),
                (int) data.get("count"));
    }
}
