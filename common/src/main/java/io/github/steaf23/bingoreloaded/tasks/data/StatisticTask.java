package io.github.steaf23.bingoreloaded.tasks.data;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.TaskDisplayMode;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.helper.TaskFormatting;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.object.ObjectContents;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record StatisticTask(StatisticHandle statistic, int count, Set<String> tags) implements TaskData {

	public StatisticTask(StatisticHandle statistic) {
		this(statistic, 1);
	}

	public StatisticTask(StatisticHandle statistic, int count) {
        this(statistic, count, new HashSet<>());
	}

	public StatisticTask(StatisticHandle statistic, int count, Set<String> tags) {
		this.statistic = statistic;
		this.count = Math.clamp(count, 1, 64);
		this.tags = tags;
	}

	@Override
	public TaskType getType() {
		return TaskType.STATISTIC;
	}

	@Override
	public Component getName(TaskFormatting formatting) {
		return switch(statistic.statisticType().getCategory()) {
			case ROOT_STATISTIC -> {
				if (statistic.statisticType().equals(StatisticType.KILL_ENTITY)) {
					yield formatting.statisticKillEntityComponent(this);
				} else if (statistic.statisticType().equals(StatisticType.ENTITY_KILLED_BY)) {
					yield formatting.statisticKilledByEntityComponent(this);
				} else {
					yield formatting.statisticItemComponent(this);
				}
			}
			case TRAVEL -> formatting.statisticNameComponent(this, Component.text(" ").append(Component.translatable("soundCategory.block")), 10);
			case DAMAGE -> formatting.statisticNameComponent(this, Component.object(ObjectContents.sprite(Key.key("gui"), Key.key("hud/heart/full"))), 1);
			default -> formatting.statisticNameComponent(this, Component.empty(), 1);
		};
	}

	@Override
	public Component[] getItemDescription(TaskFormatting formatting) {
		return BingoMessage.LORE_STATISTIC.asMultiline(NamedTextColor.DARK_AQUA);
	}

	@Override
	public Component getChatDescription(TaskFormatting formatting) {
		return Component.text().append(getItemDescription(formatting)).build();
	}

	@Override
	public boolean isTaskEqual(TaskData other) {
		if (!(other instanceof StatisticTask statisticTask))
			return false;

		return statistic.equals(statisticTask.statistic);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StatisticTask that = (StatisticTask) o;
		return statistic.equals(that.statistic);
	}

	@Override
	public int hashCode() {
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
		return new StatisticTask(statistic, newAmount, tags);
	}

}
