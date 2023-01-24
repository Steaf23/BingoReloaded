package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.itemtext.ItemText;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("ItemTask")
public record ItemTask(Material material, int count) implements TaskData
{
    @Override
    public ItemText getDisplayName()
    {
        ItemText text = new ItemText();
        text.addText(Integer.toString(count) + "x ");
        text.addItemName(material);
        return text;
    }

    @Override
    public ItemText getDescription()
    {
        return new ItemText(TranslationData.translate("game.item.lore", Integer.toString(count)));
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
        ItemTask rec = new ItemTask(Material.BEDROCK, 1);
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
