package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.ItemText;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record StatisticTask(BingoStatistic statistic, int count) implements TaskData
{
    public StatisticTask(BingoStatistic statistic)
    {
        this(statistic, 1);
    }

    @Override
    public ItemText getItemDisplayName()
    {
        ItemText amount = new ItemText(Integer.toString(count));

        ItemText text = new ItemText("*", ChatColor.ITALIC);

        switch (statistic.getCategory())
        {
            case ROOT_STATISTIC -> {
                if (statistic.stat() == Statistic.ENTITY_KILLED_BY || statistic.stat() == Statistic.KILL_ENTITY)
                {
                    ItemText entityName = new ItemText().addEntityName(statistic.entityType());
                    ItemText[] inPlaceArguments =
                            switch (statistic.stat())
                                    {
                                        case KILL_ENTITY -> new ItemText[]{amount, entityName};
                                        case ENTITY_KILLED_BY -> new ItemText[]{entityName, amount};
                                        default -> new ItemText[]{};
                                    };
                    text.addStatistic(statistic.stat(), inPlaceArguments);
                }
                else
                {
                    text.add(amount);
                    text.addText(" ");
                    text.addStatistic(statistic.stat());
                    text.addText(" ");
                    text.addItemName(statistic.materialType());
                }
            }
            case TRAVEL -> {
                text.add(amount);
                text.addText(" Blocks ");
                text.addStatistic(statistic.stat());
            }
            default -> {
                text.add(amount);
                text.addText(" ");
                text.addStatistic(statistic.stat());
            }
        }
        text.addText("*");
        return text;
    }

    @Override
    public ItemText[] getItemDescription()
    {
        Set<ChatColor> modifiers = new HashSet<>(){{
            add(ChatColor.DARK_AQUA);
        }};
        return TranslationData.translateToItemText("game.item.lore_statistic", modifiers);
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
        stream.set(BingoTask.getTaskDataKey("statistic"), PersistentDataType.STRING, statistic.stat().name());
        if (statistic.materialType() != null)
        {
            stream.set(BingoTask.getTaskDataKey("item"),  PersistentDataType.STRING, statistic.materialType().name());
        }
        if (statistic.entityType() != null)
        {
            stream.set(BingoTask.getTaskDataKey("entity"), PersistentDataType.STRING, statistic.entityType().name());
        }
        stream.set(BingoTask.getTaskDataKey("count"),  PersistentDataType.INTEGER, count);
        return stream;
    }

    public static StatisticTask fromPdc(PersistentDataContainer pdc)
    {
        Statistic stat = Statistic.valueOf(pdc.getOrDefault(BingoTask.getTaskDataKey("statistic"), PersistentDataType.STRING, "stat.minecraft.bell_ring"));

        Material item = null;
        if (pdc.has(BingoTask.getTaskDataKey("item"), PersistentDataType.STRING))
        {
            item = Material.valueOf(pdc.get(BingoTask.getTaskDataKey("item"), PersistentDataType.STRING));
        }
        EntityType entity = null;
        if (pdc.has(BingoTask.getTaskDataKey("entity"), PersistentDataType.STRING))
        {
            entity = EntityType.valueOf(pdc.get(BingoTask.getTaskDataKey("entity"), PersistentDataType.STRING));
        }
        int count = pdc.getOrDefault(BingoTask.getTaskDataKey("count"), PersistentDataType.INTEGER, 1);

        StatisticTask task = new StatisticTask(new BingoStatistic(stat, entity, item), count);
        return task;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize()
    {
        return new HashMap<>(){{
            put("statistic", statistic);
            put("count", count);
        }};
    }

    public static StatisticTask deserialize(Map<String, Object> data)
    {
        return new StatisticTask(
                (BingoStatistic) data.get("statistic"),
                (int)data.get("count")
        );
    }
}
