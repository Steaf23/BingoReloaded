package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.bingoreloaded.data.core.node.NodeBuilder;
import io.github.steaf23.playerdisplay.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    public StatisticTask(BranchNode node) {
        this(node.getSerializable("statistic", BingoStatistic.class), node.getInt("count"));
    }

    @Override
    public BranchNode toNode() {
        return new NodeBuilder()
                .withSerializable("statistic", statistic)
                .withInt("count", count)
                .getNode();
    }

    @Override
    public Component getName()
    {
        Component amount = Component.text(count);

        TextComponent.Builder builder = Component.text().append(Component.text("*"))
                .color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.ITALIC);

        switch (statistic.getCategory())
        {
            case ROOT_STATISTIC -> {
                if (statistic.stat() == Statistic.KILL_ENTITY)
                {
                    Component entityName = ComponentUtils.entityName(statistic.entityType());
                    Component[] inPlaceArguments = new Component[]{amount, Component.empty()};
                    builder.append(ComponentUtils.statistic(statistic.stat(), inPlaceArguments))
                            .append(Component.text(" ("))
                            .append(entityName)
                            .append(Component.text(")"));
                }
                else if (statistic.stat() == Statistic.ENTITY_KILLED_BY) {
                    Component entityName = ComponentUtils.entityName(statistic.entityType());
                    Component[] inPlaceArguments = new Component[]{Component.empty(), amount, Component.empty()};
                    builder.append(Component.text(" ("))
                            .append(entityName)
                            .append(Component.text(")"))
                            .append(ComponentUtils.statistic(statistic.stat(), inPlaceArguments));
                }
                else
                {
                    builder.append(ComponentUtils.statistic(statistic.stat()))
                            .append(Component.text(" "))
                            .append(ComponentUtils.itemName(statistic.materialType()))
                            .append(Component.text(": "))
                            .append(amount);
                }
            }
            case TRAVEL -> builder.append(ComponentUtils.statistic(statistic.stat()))
                    .append(Component.text(": "))
                    .append(Component.text(count * 10))
                    .append(Component.text(" Blocks"));

            default -> builder.append(ComponentUtils.statistic(statistic.stat()))
                    .append(Component.text(": "))
                    .append(amount);
        }
        builder.append(Component.text("*"));
        return builder.build();
    }

    @Override
    public Component[] getItemDescription()
    {
        return BingoMessage.LORE_STATISTIC.asMultiline(NamedTextColor.DARK_AQUA);
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
    public @NotNull PersistentDataContainer pdcSerialize(PersistentDataContainer stream)
    {
        stream.set(GameTask.getTaskDataKey("statistic"), PersistentDataType.STRING, statistic.stat().name());
        if (statistic.materialType() != null)
        {
            stream.set(GameTask.getTaskDataKey("item"),  PersistentDataType.STRING, statistic.materialType().name());
        }
        if (statistic.entityType() != null)
        {
            stream.set(GameTask.getTaskDataKey("entity"), PersistentDataType.STRING, statistic.entityType().name());
        }
        stream.set(GameTask.getTaskDataKey("count"),  PersistentDataType.INTEGER, count);
        return stream;
    }

    public static StatisticTask fromPdc(PersistentDataContainer pdc)
    {
        Statistic stat = Statistic.valueOf(pdc.getOrDefault(GameTask.getTaskDataKey("statistic"), PersistentDataType.STRING, "stat.minecraft.bell_ring"));

        Material item = null;
        if (pdc.has(GameTask.getTaskDataKey("item"), PersistentDataType.STRING))
        {
            item = Material.valueOf(pdc.get(GameTask.getTaskDataKey("item"), PersistentDataType.STRING));
        }
        EntityType entity = null;
        if (pdc.has(GameTask.getTaskDataKey("entity"), PersistentDataType.STRING))
        {
            entity = EntityType.valueOf(pdc.get(GameTask.getTaskDataKey("entity"), PersistentDataType.STRING));
        }
        int count = pdc.getOrDefault(GameTask.getTaskDataKey("count"), PersistentDataType.INTEGER, 1);

        return new StatisticTask(new BingoStatistic(stat, entity, item), count);
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
