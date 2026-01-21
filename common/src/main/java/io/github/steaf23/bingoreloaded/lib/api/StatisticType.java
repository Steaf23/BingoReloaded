package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface StatisticType extends Keyed {
	StatisticType KILL_ENTITY = StatisticType.of(Key.key("minecraft", "kill_entity"));
	StatisticType ENTITY_KILLED_BY = StatisticType.of(Key.key("minecraft", "entity_killed_by"));

	enum StatisticCategory
	{
		TRAVEL,
		BLOCK_INTERACT,
		CONTAINER_INTERACT,
		DAMAGE,
		ROOT_STATISTIC,
		OTHER,
	}

	static StatisticType of(Key key) {
		return PlatformResolver.get().resolveStatisticType(key);
	}

	boolean equals(Object other);

	StatisticCategory getCategory();

	boolean isSubStatistic();

	String translationKey();

	boolean getsUpdatedAutomatically();

	@NotNull ItemType icon(StatisticDefinition fullStatistic);
}
