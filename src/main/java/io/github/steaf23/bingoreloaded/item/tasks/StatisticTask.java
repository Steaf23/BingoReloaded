package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.itemtext.ItemText;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("StatisticTask")
public record StatisticTask(BingoStatistic statistic, int count) implements TaskData
{
    @Override
    public ItemText getDisplayName()
    {
        ItemText amount = new ItemText(Integer.toString(count));

        ItemText text = new ItemText("*");

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
    public ItemText getDescription()
    {
        return new ItemText(TranslationData.translate("game.item.lore_statistic"));
    }

    @Override
    public PersistentDataContainer pdcSerialize(PersistentDataContainer stream)
    {
        stream.set(BingoTask.getTaskDataKey("statistic"),  PersistentDataType.TAG_CONTAINER, statistic.asPdc());
        stream.set(BingoTask.getTaskDataKey("count"),  PersistentDataType.INTEGER, count);
        return stream;
    }

    public static StatisticTask fromPdc(PersistentDataContainer pdc)
    {
        StatisticTask task = new StatisticTask(new BingoStatistic(Statistic.BELL_RING), 1);
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
