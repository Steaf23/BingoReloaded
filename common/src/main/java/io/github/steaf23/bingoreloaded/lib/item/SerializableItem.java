package io.github.steaf23.bingoreloaded.lib.item;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import org.jetbrains.annotations.NotNull;


public record SerializableItem(int slot, @NotNull StackHandle stack) {

	public static final DataStorageSerializer<SerializableItem> SERIALIZER = DataStorageSerializer.of(SerializableItem.class,
			(storage, value) -> {
				storage.setInt("slot", value.slot());
				storage.setItemStack("stack", value.stack());
			}, storage -> {
				return new SerializableItem(storage.getInt("slot", 0), storage.getItemStack("stack"));
			});

	public static SerializableItem fromItemTemplate(ItemTemplate template) {
		return new SerializableItem(template.getSlot(), template.buildItem());
	}
}