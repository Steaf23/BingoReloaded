package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("Bingo.CustomKit")
public record CustomKit(String name, PlayerKit slot, List<SerializableItem> items, int cardSlot) implements ConfigurationSerializable
{
    @NotNull
    @Override
    public Map<String, Object> serialize()
    {
        Map<String, Object> data = new HashMap<>();

        int slotId = switch (slot)
        {
            case CUSTOM_1 -> 1;
            case CUSTOM_2 -> 2;
            case CUSTOM_3 -> 3;
            case CUSTOM_4 -> 4;
            case CUSTOM_5 -> 5;
            default -> 0;
        };

        if (slotId == 0)
            return data;

        data.put("name", name);
        data.put("slot", slotId);
        data.put("items", items);
        data.put("card_slot", cardSlot);
        return data;
    }

    public static CustomKit deserialize(Map<String, Object> data)
    {
        PlayerKit kit = switch ((int)data.get("slot"))
        {
            case 1 -> PlayerKit.CUSTOM_1;
            case 2 -> PlayerKit.CUSTOM_2;
            case 3 -> PlayerKit.CUSTOM_3;
            case 4 -> PlayerKit.CUSTOM_4;
            case 5 -> PlayerKit.CUSTOM_5;
            default -> throw new IllegalStateException("Unexpected value: " + (int) data.get("slot"));
        };
        return new CustomKit((String)data.get("name"), kit, (List<SerializableItem>)data.get("items"), (int)data.getOrDefault("card_slot", 40));
    }

    public static CustomKit fromPlayerInventory(Player player, String kitName, PlayerKit kitSlot)
    {
        List<SerializableItem> items = new ArrayList<>();
        int slot = 0;
        int cardSlot = 40;
        for (ItemStack itemStack : player.getInventory())
        {
            if (itemStack != null) {
                // if this item is the card, save the slot instead and disregard the item itself.
                if (PlayerKit.CARD_ITEM.isCompareKeyEqual(itemStack)) {
                    cardSlot = slot;
                }
                else {
                    items.add(new SerializableItem(slot, itemStack));
                }
            }
            slot += 1;
        }

        return new CustomKit(kitName, kitSlot, items, cardSlot);
    }

    public String getName()
    {
        return BingoMessage.convertColors(name);
    }
}
