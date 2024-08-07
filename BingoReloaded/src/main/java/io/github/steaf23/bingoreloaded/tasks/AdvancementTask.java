package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.playerdisplay.util.ComponentUtils;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Bingo.AdvancementTask")
public record AdvancementTask(Advancement advancement) implements TaskData
{
    public AdvancementTask(Advancement advancement)
    {
        this.advancement = advancement;
    }

    @Override
    public Component getName()
    {
        var builder = Component.text().append(Component.text("["))
                .color(NamedTextColor.GREEN).decorate(TextDecoration.ITALIC);

        if (advancement == null)
        {
            ConsoleMessenger.log("Could not get advancement, returning null!");
            builder.append(Component.text("no advancement?"));
        }
        else
        {
            builder.append(ComponentUtils.advancementTitle(advancement));
        }
        builder.append(Component.text("]"));
        return builder.build();
    }

    @Override
    public Component[] getItemDescription()
    {
        return BingoMessage.LORE_ADVANCEMENT.asMultiline(NamedTextColor.DARK_AQUA);
    }

    // This method exists because advancement descriptions can contain newlines,
    // which makes it impossible to use as item names or descriptions without getting a missing character.
    @Override
    public Component getChatDescription()
    {
        return ComponentUtils.advancementDescription(advancement)
                .color(NamedTextColor.DARK_AQUA);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvancementTask that = (AdvancementTask) o;
        return advancement.getKey().equals(that.advancement.getKey());
    }

    @Override
    public int hashCode()
    {
        return advancement.getKey().hashCode();
    }

    @Override
    public boolean isTaskEqual(TaskData other)
    {
        return this.equals(other);
    }

    @Override
    public @NotNull PersistentDataContainer pdcSerialize(PersistentDataContainer stream)
    {
        stream.set(GameTask.getTaskDataKey("advancement"), PersistentDataType.STRING, advancement.getKey().toString());
        return stream;
    }

    public static AdvancementTask fromPdc(PersistentDataContainer pdc)
    {
        Advancement a = Bukkit.getAdvancement(NamespacedKey.fromString(
                        pdc.getOrDefault(GameTask.getTaskDataKey("advancement"), PersistentDataType.STRING, "minecraft:story/mine_stone")));
        return new AdvancementTask(a);
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
