package io.github.steaf23.bingoreloaded.lib.api.statistics;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public record VanillaStatistic(String keyStr, Category category, Function<StatisticHandle, ItemType> iconFunction) implements Keyed {

	public enum Category
	{
		TRAVEL,
		BLOCK_INTERACT,
		CONTAINER_INTERACT,
		DAMAGE,
		ROOT_STATISTIC,
		OTHER,
	}

	@Override
	public @NonNull Key key() {
		return Key.key(keyStr);
	}

	public ItemType icon(StatisticHandle handle) {
		return iconFunction.apply(handle);
	}
}
