package io.github.steaf23.bingoreloaded.lib.api.statistics;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticCategory;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public record VanillaStatistic(String keyStr, StatisticCategory category, Function<StatisticHandle, ItemType> iconFunction) implements Keyed {

	@Override
	public @NonNull Key key() {
		return switch (category.type) {
			case CUSTOM -> Key.key("custom");
			case ITEM, BLOCK, ENTITY -> Key.key(keyStr());
		};
	}

	public Key type() {
		return key();
	}

	public Key specification(StatisticHandle handle) {
		return switch (category.type) {
			case CUSTOM -> key();
			case ITEM, BLOCK -> handle.itemType().key();
			case ENTITY -> handle.entityType().key();
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
}
