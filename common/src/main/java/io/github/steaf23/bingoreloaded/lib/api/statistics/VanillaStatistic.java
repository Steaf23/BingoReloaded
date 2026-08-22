package io.github.steaf23.bingoreloaded.lib.api.statistics;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public record VanillaStatistic(String keyStr, Category category, Function<StatisticHandle, ItemType> iconFunction, Specification specification) implements Keyed {

	public enum Category
	{
		TRAVEL,
		BLOCK_INTERACT,
		CONTAINER_INTERACT,
		DAMAGE,
		ROOT_STATISTIC,
		OTHER,
	}

	public enum Specification
	{
		NONE,
		ITEM,
		ENTITY,
	}

	@Override
	public @NonNull Key key() {
		return Key.key(keyStr);
	}

	public ItemType icon(StatisticHandle handle) {
		return iconFunction.apply(handle);
	}

	public boolean getsUpdatedOften() {
		if (category() == VanillaStatistic.Category.TRAVEL) {
			return true;
		} else return this == VanillaStatistics.PLAY_TIME ||
				this == VanillaStatistics.SNEAK_TIME ||
				this == VanillaStatistics.TOTAL_WORLD_TIME ||
				this == VanillaStatistics.TIME_SINCE_REST ||
				this == VanillaStatistics.TIME_SINCE_DEATH;
	}
}
