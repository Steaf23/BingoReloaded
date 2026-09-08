package io.github.steaf23.bingoreloaded.lib.api.statistics;

import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;


public record StatisticHandle(@NotNull VanillaStatistic type, @Nullable EntityType entityType, @Nullable ItemType itemType) {

	public static final DataStorageSerializer<StatisticHandle> SERIALIZER = DataStorageSerializer.of(
			(storage, value) -> {
				storage.setNamespacedKey("stat_type", value.type.key());

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
				VanillaStatistic type = VanillaStatistics.fromKey(storage.getNamespacedKey("stat_type"));

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

				return new StatisticHandle(type, entity, item);
			});

	public StatisticHandle(VanillaStatistic type) {
		this(type, null, null);
	}

	public StatisticHandle(VanillaStatistic type, @Nullable ItemType item) {
		this(type, null, item);
	}

	public StatisticHandle(VanillaStatistic type, @Nullable EntityType entity) {
		this(type, entity, null);
	}

	public boolean isSubStatistic() {
		return type.category().type != StatisticCategory.Type.CUSTOM;
	}

	public String translationKey() {
		return StatisticsKeyConverter.getMinecraftTranslationKey(type);
	}

	public boolean hasItemType() {
		return itemType() != null;
	}

	public boolean hasEntity() {
		return entityType() != null;
	}

	public ItemType icon() {
		return type().icon(this);
	}

	public static Set<EntityType> getValidEntityTypes(BingoReloadedRuntime runtime) {
		return runtime.getValidEntityTypesForStatistics();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StatisticHandle(VanillaStatistic otherType, EntityType otherEntity, ItemType otherItem))) {
			return false;
		}

		return otherType.equals(type) &&  Objects.equals(otherEntity, entityType) && Objects.equals(otherItem, itemType);
	}
}
