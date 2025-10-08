package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.DefaultKitData;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultKitStorageSerializer implements DataStorageSerializer<DefaultKitData.Kit> {

	@Override
	public void toDataStorage(@NotNull DataStorage storage, DefaultKitData.@NotNull Kit value) {
		storage.setSerializableList("items", SerializableItem.class, value.items());
	}

	@Override
	public @Nullable DefaultKitData.Kit fromDataStorage(@NotNull DataStorage storage) {

		List<SerializableItem> items = storage.getSerializableList("items", SerializableItem.class);
		return new DefaultKitData.Kit(items);
	}
}
