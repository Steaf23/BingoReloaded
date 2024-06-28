package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SerializableAs("Bingo.StatisticTask")
public record StatisticTask(BingoStatistic statistic, int count) implements CountableTask
{
    public StatisticTask(BingoStatistic statistic)
    {
        this(statistic, 1);
    }

    public StatisticTask(BingoStatistic statistic, int count)
    {
        this.statistic = statistic;
        this.count = Math.min(64, Math.max(1, count));
    }

    @Override
    public Component getName()
    {
        TextComponent amount = new TextComponent(Integer.toString(count));

        Component nameComponent = Component.text("*").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.ITALIC);

        return nameComponent;

        //FIXME: reimplement
//
//        switch (statistic.getCategory())
//        {
//            case ROOT_STATISTIC -> {
//                if (statistic.stat() == Statistic.KILL_ENTITY)
//                {
//                    BaseComponent entityName = ChatComponentUtils.entityName(statistic.entityType());
//                    BaseComponent[] inPlaceArguments = new BaseComponent[]{amount, new TextComponent("")};
//                    builder.append(ChatComponentUtils.statistic(statistic.stat(), inPlaceArguments))
//                            .append(" (")
//                            .append(entityName)
//                            .append(")");
//                }
//                else if (statistic.stat() == Statistic.ENTITY_KILLED_BY) {
//                    BaseComponent entityName = ChatComponentUtils.entityName(statistic.entityType());
//                    BaseComponent[] inPlaceArguments = new BaseComponent[]{new TextComponent(""), amount};
//                    builder.append("(")
//                            .append(entityName)
//                            .append(") ")
//                            .append(ChatComponentUtils.statistic(statistic.stat(), inPlaceArguments));
//                }
//                else
//                {
//                    builder.append(ChatComponentUtils.statistic(statistic.stat()))
//                            .append(" ")
//                            .append(ChatComponentUtils.itemName(statistic.materialType()))
//                            .append(": ")
//                            .append(amount);
//                }
//            }
//            case TRAVEL -> {
//                builder.append(ChatComponentUtils.statistic(statistic.stat()))
//                        .append(": ")
//                        .append(new TextComponent(Integer.toString(count * 10)))
//                        .append(" Blocks");
//            }
//            default -> {
//                builder.append(ChatComponentUtils.statistic(statistic.stat()))
//                        .append(": ")
//                        .append(amount);
//            }
//        }
//        builder.append("*");
//        return builder.build();
    }

    @Override
    public Component[] getItemDescription()
    {
        //FIXME: make dark aqua
        //TODO: make MultilineComponent builder class??
        return BingoTranslation.LORE_STATISTIC.asComponent();
    }

    @Override
    public Component getChatDescription()
    {
        return Component.text().append(getItemDescription()).build();
    }

    @Override
    public boolean isTaskEqual(TaskData other)
    {
        if (!(other instanceof StatisticTask statisticTask))
            return false;

        return statistic.equals(statisticTask.statistic);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticTask that = (StatisticTask) o;
        return Objects.equals(statistic, that.statistic);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(statistic);
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

    @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public CountableTask updateTask(int newCount)
    {
        return new StatisticTask(statistic, newCount);
    }
}
