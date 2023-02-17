package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.ItemText;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SerializableAs("Bingo.ItemTask")
public record ItemTask(Material material, int count) implements CountableTask
{
    public ItemTask(Material material)
    {
        this(material, 1);
    }

    public ItemTask(Material material, int count)
    {
        this.material = material;
        this.count = Math.min(64, Math.max(1, count));
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

    @Override
    public boolean isTaskEqual(TaskData other)
    {
        if (!(other instanceof ItemTask itemTask))
            return false;

        return material.equals(itemTask.material);
    }

    public static ItemTask deserialize(Map<String, Object> data)
    {
        return new ItemTask(
                Material.valueOf((String) data.get("item")),
                (int) data.get("count"));
    }

    @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public CountableTask updateTask(int newCount)
    {
        return new ItemTask(material, newCount);
    }
}
