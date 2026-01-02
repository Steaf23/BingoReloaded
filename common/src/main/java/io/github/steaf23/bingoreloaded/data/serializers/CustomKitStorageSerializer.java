package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import org.jetbrains.annotations.NotNull;

public class CustomKitStorageSerializer implements DataStorageSerializer<CustomKit>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull CustomKit value) {
        storage.setByte("card_slot", (byte)value.cardSlot());
        storage.setString("name", ComponentUtils.MINI_BUILDER.serialize(value.name()));
        storage.setByte("kit_id", slotFromKit(value.slot()));
        storage.setSerializableList("items", SerializableItem.class, value.items());
    }

    @Override
    public CustomKit fromDataStorage(@NotNull DataStorage storage) {
        return new CustomKit(ComponentUtils.MINI_BUILDER.deserialize(storage.getString("name", "")),
                kitFromSlot(storage.getByte("kit_id", (byte)0)),
                storage.getSerializableList("items", SerializableItem.class),
                storage.getByte("card_slot", (byte)40)); //off-hand slot
    }

    private static PlayerKit kitFromSlot(byte slot) throws IllegalStateException {
        return switch (slot)
        {
            case 1 -> PlayerKit.CUSTOM_1;
            case 2 -> PlayerKit.CUSTOM_2;
            case 3 -> PlayerKit.CUSTOM_3;
            case 4 -> PlayerKit.CUSTOM_4;
            case 5 -> PlayerKit.CUSTOM_5;
            default -> throw new IllegalStateException("Unexpected value: " + slot);
        };
    }

    private static byte slotFromKit(PlayerKit kit) throws IllegalStateException {
        return switch (kit)
        {
            case CUSTOM_1 -> 1;
            case CUSTOM_2 -> 2;
            case CUSTOM_3 -> 3;
            case CUSTOM_4 -> 4;
            case CUSTOM_5 -> 5;
            default -> throw new IllegalStateException("Unexpected kit slot for kit" + kit.getDisplayName());
        };
    }
}
