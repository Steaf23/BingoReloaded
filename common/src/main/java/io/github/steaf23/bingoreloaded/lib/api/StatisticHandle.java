package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public interface StatisticHandle {

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
		return PlatformResolver.getRegistries().createStatistic(type, item, entity);
	}

	static Set<EntityType> getValidEntityTypes(BingoReloadedRuntime runtime) {
		return runtime.getValidEntityTypesForStatistics();
	}
}
