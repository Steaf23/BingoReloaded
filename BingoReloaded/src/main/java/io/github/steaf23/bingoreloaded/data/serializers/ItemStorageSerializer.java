package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStorageSerializer implements DataStorageSerializer<SerializableItem>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull SerializableItem value) {
        storage.setInt("slot", value.slot());
        storage.setItemStack("stack", value.stack());
    }

    @Override
    public @Nullable SerializableItem fromDataStorage(@NotNull DataStorage storage) {
        return new SerializableItem(storage.getInt("slot", 0), storage.getItemStack("stack"));
    }
}
