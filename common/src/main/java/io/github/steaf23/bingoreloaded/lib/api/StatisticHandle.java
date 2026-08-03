package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public interface StatisticHandle {
	DataStorageSerializer<StatisticHandle> SERIALIZER = DataStorageSerializer.of(StatisticHandle.class,
			(storage, value) -> {
				storage.setNamespacedKey("stat_type", value.statisticType().key());

				ItemType item = value.itemType();
				if (item != null)
				{
					storage.setNamespacedKey("item", item.key());
				}
				EntityType entity = value.entityType();
				if (entity != null)
				{
					storage.setNamespacedKey("entity", entity.key());
				}
			}, storage -> {
				StatisticType type = StatisticType.of(storage.getNamespacedKey("stat_type"));

				ItemType item = null;
				if (storage.contains("item"))
				{
					item = ItemType.of(storage.getNamespacedKey("item"));
				}
				EntityType entity = null;
				if (storage.contains("entity"))
				{
					entity = EntityType.of(storage.getNamespacedKey("entity"));
				}

				return StatisticHandle.create(type, item, entity);
			});

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
