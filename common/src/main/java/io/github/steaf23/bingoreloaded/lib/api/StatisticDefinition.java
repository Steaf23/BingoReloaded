package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;


public record StatisticDefinition(@NotNull StatisticType type, @Nullable EntityType entityType,
								  @Nullable ItemType itemType) {

	private static final Set<EntityType> VALID_ENTITIES_FOR_STATISTICS = cacheValidEntityTypes();

	public StatisticDefinition(StatisticType stat) {
		this(stat, null, null);
	}

	public StatisticDefinition(StatisticType stat, @Nullable EntityType entityType) {
		this(stat, entityType, null);
	}

	public StatisticDefinition(StatisticType stat, @Nullable ItemType itemType) {
		this(stat, null, itemType);
	}

	public boolean hasItemType() {
		return itemType() != null;
	}

	public boolean hasEntity() {
		return entityType() != null;
	}

	public static Set<EntityType> getValidEntityTypes() {
		return VALID_ENTITIES_FOR_STATISTICS;
	}

	private static Set<EntityType> cacheValidEntityTypes() {
		return BingoReloaded.runtime().getValidEntityTypesForStatistics();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StatisticDefinition(StatisticType stat, EntityType entity, ItemType item))) return false;
		return Objects.equals(itemType, item) && Objects.equals(entityType, entity) && Objects.equals(type, stat);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, entityType, itemType);
	}
}
