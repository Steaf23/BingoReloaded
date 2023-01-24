package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.item.itemtext.ItemText;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("AdvancementTask")
public record AdvancementTask(Advancement advancement) implements TaskData
{
    @Override
    public ItemText getDisplayName()
    {
        ItemText text = new ItemText("[");
        text.addAdvancementTitle(advancement);
        text.addText("]");
        return text;
    }

    @Override
    public ItemText getDescription()
    {
        return new ItemText().addAdvancementDescription(advancement);
    }

    @Override
    public PersistentDataContainer pdcSerialize(PersistentDataContainer stream)
    {
        return null;
    }

    public static AdvancementTask fromPdc(PersistentDataContainer pdc)
    {
        AdvancementTask task = new AdvancementTask(null);
        return task;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize()
    {
        return new HashMap<>(){{
            put("advancement", advancement.getKey().toString());
        }};
    }

    public static AdvancementTask deserialize(Map<String, Object> data)
    {
        return new AdvancementTask(
                Bukkit.getAdvancement(NamespacedKey.fromString((String)data.get("advancement")))
        );
    }
}
