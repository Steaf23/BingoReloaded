package io.github.steaf23.bingoreloaded.lib.data.serializers;

import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.statistics.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistic;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistics;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatisticSerializer implements DataStorageSerializer<StatisticHandle> {

	@Override
	public void toDataStorage(@NotNull DataStorage storage, @NotNull StatisticHandle value) {
		storage.setNamespacedKey("stat_type", value.type().key());

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
	}

	@Override
	public @Nullable StatisticHandle fromDataStorage(@NotNull DataStorage storage) {
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
	}
}
