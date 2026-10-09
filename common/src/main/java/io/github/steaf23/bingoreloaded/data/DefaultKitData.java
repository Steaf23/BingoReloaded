package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;

import java.util.List;
import java.util.Optional;

public class DefaultKitData {
	private final DataAccessor data = BingoReloaded.getDataAccessor("data/default_kits");

	public record Kit(List<SerializableItem> items) {

		public static final DataStorageSerializer<Kit> SERIALIZER = DataStorageSerializer.of(
				(storage, value) -> {
					storage.setSerializableList("items", SerializableItem.SERIALIZER, value.items());
				}, storage -> {
					List<SerializableItem> items = storage.getSerializableList("items", SerializableItem.SERIALIZER);
					return new DefaultKitData.Kit(items);
				});
	}

	public Optional<Kit> getKit(PlayerKit slot)
	{
		return Optional.ofNullable(data.getSerializable(slot.configName, Kit.SERIALIZER));
	}
}
