package io.github.steaf23.bingoreloaded.lib.api;

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;


public interface StatisticHandle {
	Set<EntityType> VALID_ENTITIES_FOR_STATISTICS = getValidEntityTypes();

	enum StatisticCategory
	{
		TRAVEL,
		BLOCK_INTERACT,
		CONTAINER_INTERACT,
		DAMAGE,
		ROOT_STATISTIC,
		OTHER,
	}

	default boolean isEntityValid() {
		return VALID_ENTITIES_FOR_STATISTICS.contains(entity());
	}

	StatisticType type();
	@Nullable ItemType item();
	@Nullable EntityType entity();
	boolean isSubStatistic();
	String translationKey();

	default boolean hasItemType() {
		return item() != null;
	}

	default boolean hasEntity() {
		return entity() != null;
	}

	boolean getsUpdatedAutomatically();
	StatisticCategory getCategory();
	ItemType icon();

	static StatisticHandle create(StatisticType type, @Nullable ItemType item, @Nullable EntityType entity) {

	}

	private static Set<EntityType> getValidEntityTypes() {
		//FIXME: REFACTOR fix this function
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
