package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public interface StatisticHandle {
	Set<EntityType> VALID_ENTITIES_FOR_STATISTICS = cacheValidEntityTypes();

	default boolean isEntityValid() {
		return VALID_ENTITIES_FOR_STATISTICS.contains(entityType());
	}

	StatisticType statisticType();
	@Nullable ItemType itemType();
	@Nullable EntityType entityType();
	boolean isSubStatistic();
	String translationKey();

	default boolean hasItemType() {
		return itemType() != null;
	}

	default boolean hasEntity() {
		return entityType() != null;
	}

	boolean getsUpdatedAutomatically();
	ItemType icon();

	static StatisticHandle create(StatisticType type, @Nullable ItemType item, @Nullable EntityType entity) {
		return PlatformResolver.get().createStatistic(type, item, entity);
	}

	static Set<EntityType> getValidEntityTypes() {
		return VALID_ENTITIES_FOR_STATISTICS;
	}

	private static Set<EntityType> cacheValidEntityTypes() {
		return BingoReloaded.runtime().getValidEntityTypesForStatistics();
	}
}
