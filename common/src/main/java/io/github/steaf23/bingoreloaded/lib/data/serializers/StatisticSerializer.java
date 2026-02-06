package io.github.steaf23.bingoreloaded.lib.data.serializers;

import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.StatisticDefinition;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatisticSerializer implements DataStorageSerializer<StatisticDefinition> {

	@Override
	public void toDataStorage(@NotNull DataStorage storage, @NotNull StatisticDefinition value) {
		storage.setString("stat_type", value.type().key().toString());

		ItemType item = value.itemType();
		if (item != null)
		{
			storage.setItemType("item", item);
		}
		EntityType entity = value.entityType();
		if (entity != null)
		{
			storage.setString("entity", entity.key().toString());
		}
	}

	@Override
	public @Nullable StatisticDefinition fromDataStorage(@NotNull DataStorage storage) {
		StatisticType type = StatisticType.of(Key.key(storage.getString("stat_type", "")));

		ItemType item = null;
		if (storage.contains("item"))
		{
			item = storage.getItemType("item");
		}
		EntityType entity = null;
		if (storage.contains("entity"))
		{
			entity = EntityType.of(Key.key(storage.getString("entity", "")));
		}

		return new StatisticDefinition(type, entity, item);
	}
}
