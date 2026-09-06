package io.github.steaf23.bingoreloaded.lib.api.statistics;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticCategory;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public record VanillaStatistic(Key nameOrGroup, StatisticCategory category, Function<StatisticHandle, ItemType> iconFunction) implements Keyed {

	public Key group() {
		return switch (category.type) {
			case CUSTOM -> Key.key("custom");
			case ITEM, BLOCK, ENTITY -> nameOrGroup;
		};
	}

	public Key specification(StatisticHandle stat) {
		return switch (category.type) {
			case CUSTOM -> nameOrGroup;
			case ITEM, BLOCK -> stat.itemType().key();
			case ENTITY -> stat.entityType().key();
		};
	}

	public ItemType icon(StatisticHandle handle) {
		return iconFunction.apply(handle);
	}

	public boolean getsUpdatedOften() {
		if (category() == StatisticCategory.TRAVEL) {
			return true;
		} else return this == VanillaStatistics.PLAY_TIME ||
				this == VanillaStatistics.SNEAK_TIME ||
				this == VanillaStatistics.TOTAL_WORLD_TIME ||
				this == VanillaStatistics.TIME_SINCE_REST ||
				this == VanillaStatistics.TIME_SINCE_DEATH;
	}

	@Override
	public @NonNull Key key() {
		return nameOrGroup;
	}
}
