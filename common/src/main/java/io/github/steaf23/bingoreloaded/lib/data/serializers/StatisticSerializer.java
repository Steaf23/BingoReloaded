package io.github.steaf23.bingoreloaded.lib.data.serializers;

import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatisticSerializer implements DataStorageSerializer<StatisticHandle> {

	@Override
	public void toDataStorage(@NotNull DataStorage storage, @NotNull StatisticHandle value) {
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
	}

	@Override
	public @Nullable StatisticHandle fromDataStorage(@NotNull DataStorage storage) {
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
	}
}
