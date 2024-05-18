package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    public BaseComponent getName()
    {
        ComponentBuilder builder = new ComponentBuilder().color(ChatColor.YELLOW);
        builder.append(count + "x ")
                .append(ChatComponentUtils.itemName(material));
        return builder.build();
    }

    @Override
    public BaseComponent[] getItemDescription()
    {
        Set<ChatColor> modifiers = new HashSet<>(){{
            add(ChatColor.DARK_AQUA);
        }};
        return BingoTranslation.LORE_ITEM.asComponent(modifiers, new TextComponent(Integer.toString(count)));
    }

    @Override
    public BaseComponent getChatDescription()
    {
        return new ComponentBuilder().append(getItemDescription()).build();
    }

    @Override
    public @NotNull PersistentDataContainer pdcSerialize(PersistentDataContainer stream)
    {
        stream.set(BingoTask.getTaskDataKey("item"),  PersistentDataType.STRING, material.name());
        stream.set(BingoTask.getTaskDataKey("count"),  PersistentDataType.INTEGER, count);
        return stream;
    }

    public static ItemTask fromPdc(PersistentDataContainer pdc)
    {
        Material item = Material.valueOf(pdc.getOrDefault(BingoTask.getTaskDataKey("item"), PersistentDataType.STRING, "BEDROCK"));
        int count = pdc.getOrDefault(BingoTask.getTaskDataKey("count"), PersistentDataType.INTEGER, 1);
        return new ItemTask(item, count);
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
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemTask itemTask = (ItemTask) o;
        return material == itemTask.material;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(material);
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
