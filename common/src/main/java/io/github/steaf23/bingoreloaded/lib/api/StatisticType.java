package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public interface StatisticType extends Keyed {
	StatisticType KILL_ENTITY = StatisticType.of(Key.key("minecraft", "kill_entity"));
	StatisticType ENTITY_KILLED_BY = StatisticType.of(Key.key("minecraft", "entity_killed_by"));

	static StatisticType of(Key key) {
	}
}
