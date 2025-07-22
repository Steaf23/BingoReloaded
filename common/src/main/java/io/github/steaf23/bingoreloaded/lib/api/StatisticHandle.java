package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
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
		//FIXME: REFACTOR fix this function (Move to Bingo Runtime?)

		// This is the reason we cant support 1.19.2 or below, since we would have to manually add ender dragon and wither spawn eggs.
		Set<EntityType> types = new HashSet<>();
//		Arrays.stream(ItemType.values())
//				.forEach(mat -> {
//					if (mat.name().contains("_SPAWN_EGG")) {
//						types.add(EntityType.valueOf(mat.name().replace("_SPAWN_EGG", "")));
//					}
//				});
//		// Note: pre 1.20.5 mooshroom spawn egg needed to be parsed by hand
		return types;
	}
}
