package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomKitStorageSerializer implements DataStorageSerializer<CustomKit>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, CustomKit value) {
        List<ItemStack> stacks = value.items().stream().map(SerializableItem::stack).toList();
        List<Integer> slots = value.items().stream().map(SerializableItem::slot).toList();

        storage.setString("name", PlayerDisplay.MINI_BUILDER.serialize(value.name()));
        storage.setInt("slot", slotFromKit(value.slot()));
        storage.setList("item_stacks", TagDataType.ITEM_STACK, stacks);
        storage.setList("item_slots", TagDataType.INT, slots);
    }

    @Override
    public CustomKit fromDataStorage(@NotNull DataStorage storage) {
        return new CustomKit(PlayerDisplay.MINI_BUILDER.deserialize(storage.getString("name", "")),
                kitFromSlot(storage.getInt("slot", 0)),
                createSerializableItems(
                        storage.getList("item_stacks", TagDataType.ITEM_STACK),
                        storage.getList("item_slots", TagDataType.INT)),
                storage.getInt("card_slot", 40));
    }

    private static List<SerializableItem> createSerializableItems(List<ItemStack> stacks, List<Integer> slots) {
        if (stacks.size() != slots.size()) {
            return List.of();
        }

        List<SerializableItem> result = new ArrayList<>();
        for (int i = 0; i < stacks.size(); i++) {
            result.add(new SerializableItem(slots.get(i), stacks.get(i)));
        }
        return result;
    }

    private static PlayerKit kitFromSlot(int slot) throws IllegalStateException {
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

    private static int slotFromKit(PlayerKit kit) throws IllegalStateException {
        return switch (kit)
        {
            case CUSTOM_1 -> 1;
            case CUSTOM_2 -> 2;
            case CUSTOM_3 -> 3;
            case CUSTOM_4 -> 4;
            case CUSTOM_5 -> 5;
            default -> throw new IllegalStateException("Unexpected kit" + kit.getCardSlot());
        };
    }
}
