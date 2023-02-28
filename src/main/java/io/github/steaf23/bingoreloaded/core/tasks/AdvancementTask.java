package io.github.steaf23.bingoreloaded.core.tasks;

import io.github.steaf23.bingoreloaded.core.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.ItemText;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SerializableAs("Bingo.AdvancementTask")
public record AdvancementTask(Advancement advancement) implements TaskData
{
    @Override
    public ItemText getItemDisplayName()
    {
        ItemText text = new ItemText("[", ChatColor.ITALIC);
        if (advancement == null)
        {
            Message.log("Could not get advancement, returning null!");
            text.addText("no advancement?");
        }
        else
        {
            text.addAdvancementTitle(advancement);
        }
        text.addText("]");
        return text;
    }

    @Override
    public ItemText[] getItemDescription()
    {
        ItemText desc = new ItemText(TranslationData.translate("game.item.lore_advancement"), ChatColor.DARK_AQUA);
        return new ItemText[]{desc};
    }

    // This method exists because advancement descriptions can contain newlines,
    // which makes it impossible to use as item names or descriptions without getting a missing character.
    @Override
    public BaseComponent getDescription()
    {
        BaseComponent comp = new ItemText().addAdvancementDescription(advancement).asComponent();
        comp.setColor(ChatColor.DARK_AQUA);
        return comp;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvancementTask that = (AdvancementTask) o;
        return Objects.equals(advancement, that.advancement);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(advancement);
    }

    @Override
    public int getStackSize()
    {
        return 1;
    }

    @Override
    public boolean isTaskEqual(TaskData other)
    {
        return this.equals(other);
    }

    @Override
    public PersistentDataContainer pdcSerialize(PersistentDataContainer stream)
    {
        stream.set(BingoTask.getTaskDataKey("advancement"), PersistentDataType.STRING, advancement.getKey().toString());
        return stream;
    }

    public static AdvancementTask fromPdc(PersistentDataContainer pdc)
    {
        Advancement a = Bukkit.getAdvancement(NamespacedKey.fromString(
                        pdc.getOrDefault(BingoTask.getTaskDataKey("advancement"), PersistentDataType.STRING, "minecraft:story/mine_stone")));
        AdvancementTask task = new AdvancementTask(a);
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
