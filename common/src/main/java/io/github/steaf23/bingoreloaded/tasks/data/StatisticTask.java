package io.github.steaf23.bingoreloaded.tasks.data;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.TaskDisplayMode;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Objects;

public record StatisticTask(StatisticHandle statistic, int count) implements TaskData
{
    public StatisticTask(StatisticHandle statistic)
    {
        this(statistic, 1);
    }

    public StatisticTask(StatisticHandle statistic, int count)
    {
        this.statistic = statistic;
        this.count = Math.min(64, Math.max(1, count));
    }

    @Override
    public TaskType getType() {
        return TaskType.STATISTIC;
    }

    @Override
    public Component getName()
    {
        Component amount = Component.text(count);

        TextComponent.Builder builder = Component.text().append(Component.text("*"))
                .color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.ITALIC);

        switch (statistic.statisticType().getCategory())
        {
            case ROOT_STATISTIC -> {
                if (statistic.statisticType().equals(StatisticType.KILL_ENTITY))
                {
                    Component entityName = ComponentUtils.entityName(statistic.entityType());
                    Component[] inPlaceArguments = new Component[]{amount, Component.empty()};
                    builder.append(ComponentUtils.statistic(statistic, inPlaceArguments))
                            .append(Component.text(" ("))
                            .append(entityName)
                            .append(Component.text(")"));
                }
                else if (statistic.statisticType().equals(StatisticType.ENTITY_KILLED_BY)) {
                    Component entityName = ComponentUtils.entityName(statistic.entityType());
                    Component[] inPlaceArguments = new Component[]{Component.empty(), amount, Component.empty()};
                    builder.append(Component.text(" ("))
                            .append(entityName)
                            .append(Component.text(")"))
                            .append(ComponentUtils.statistic(statistic, inPlaceArguments));
                }
                else
                {
                    builder.append(ComponentUtils.statistic(statistic))
                            .append(Component.text(" "))
                            .append(ComponentUtils.itemName(statistic.itemType()))
                            .append(Component.text(": "))
                            .append(amount);
                }
            }
            case TRAVEL -> builder.append(ComponentUtils.statistic(statistic))
                    .append(Component.text(": "))
                    .append(Component.text(count * 10))
                    .append(Component.text(" Blocks"));

            default -> builder.append(ComponentUtils.statistic(statistic))
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
        return statistic.equals(that.statistic);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(statistic);
    }

    @Override
    public boolean shouldItemGlow() {
        return true;
    }

    @Override
    public ItemType getDisplayMaterial(CardDisplayInfo context) {
        if (context.statisticDisplay() == TaskDisplayMode.GENERIC_TASK_ITEMS) {
            return ItemType.of("globe_banner_pattern");
        } else {
            return statistic().icon();
        }
    }

    @Override
    public int getRequiredAmount() {
        return count;
    }

    @Override
    public TaskData setRequiredAmount(int newAmount) {
        return new StatisticTask(statistic, newAmount);
    }

}
