package io.github.steaf23.bingoreloaded.lib.api.statistics;

import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public record StatisticHandle(@NotNull VanillaStatistic type, @Nullable EntityType entityType, @Nullable ItemType itemType) {

	public static final DataStorageSerializer<StatisticHandle> SERIALIZER = DataStorageSerializer.of(StatisticHandle.class,
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
		return type.category() == VanillaStatistic.Category.ROOT_STATISTIC;
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

	public boolean getsUpdatedAutomatically() {
		if (type().category() == VanillaStatistic.Category.TRAVEL) {
			return false;
		} else return
				type() != VanillaStatistics.PLAY_ONE_MINUTE &&
				type() != VanillaStatistics.SNEAK_TIME &&
				type() != VanillaStatistics.TOTAL_WORLD_TIME &&
				type() != VanillaStatistics.TIME_SINCE_REST &&
				type() != VanillaStatistics.TIME_SINCE_DEATH;
	}

	public ItemType icon() {
		return type().icon(this);
	}

	public static Set<EntityType> getValidEntityTypes(BingoReloadedRuntime runtime) {
		return runtime.getValidEntityTypesForStatistics();
	}
}
