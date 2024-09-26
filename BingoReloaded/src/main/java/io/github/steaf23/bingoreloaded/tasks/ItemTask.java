package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.playerdisplay.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
    public Component getName()
    {
        return Component.text().color(NamedTextColor.YELLOW)
                .append(Component.text(count + "x "))
                .append(ComponentUtils.itemName(material)).build();
    }

    @Override
    public Component[] getItemDescription()
    {
        return BingoMessage.LORE_ITEM.asMultiline(NamedTextColor.DARK_AQUA, Component.text(count));
    }

    @Override
    public Component getChatDescription()
    {
        return Component.text().append(getItemDescription()).build();
    }

    @Override
    public @NotNull PersistentDataContainer pdcSerialize(PersistentDataContainer stream)
    {
        stream.set(GameTask.getTaskDataKey("item"),  PersistentDataType.STRING, material.name());
        stream.set(GameTask.getTaskDataKey("count"),  PersistentDataType.INTEGER, count);
        return stream;
    }

    public static ItemTask fromPdc(PersistentDataContainer pdc)
    {
        Material item = Material.valueOf(pdc.getOrDefault(GameTask.getTaskDataKey("item"), PersistentDataType.STRING, "BEDROCK"));
        int count = pdc.getOrDefault(GameTask.getTaskDataKey("count"), PersistentDataType.INTEGER, 1);
        return new ItemTask(item, count);
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
