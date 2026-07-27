package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.DimensionType;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

public interface PlatformRegistries {

	ItemType resolveItemType(Key key);
	DimensionType resolveDimensionType(Key key);
	EntityType resolveEntityType(Key key);
	AdvancementHandle resolveAdvancement(PlatformServer server, Key key);
	StatisticType resolveStatisticType(Key key);
	StatusEffectType resolvePotionEffectType(Key key);

	StatisticHandle createStatistic(StatisticType type, @Nullable ItemType item, @Nullable EntityType entity);

	boolean areAdvancementsDisabled();
}
