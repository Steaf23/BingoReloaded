package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
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
public record CustomKit(String name, PlayerKit slot, List<MenuItem> items) implements ConfigurationSerializable
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
            return null;

        data.put("name", name);
        data.put("slot", slotId);
        data.put("items", items);
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
        return new CustomKit((String)data.get("name"), kit, (List<MenuItem>)data.get("items"));
    }

    public static CustomKit fromPlayerInventory(Player player, String kitName, PlayerKit kitSlot)
    {
        List<MenuItem> items = new ArrayList<>();
        int slot = 0;
        for (ItemStack itemStack : player.getInventory())
        {
            if (itemStack != null)
                items.add(new MenuItem(slot, itemStack));
            slot += 1;
        }
        return new CustomKit(kitName, kitSlot, items);
    }

    public String getName()
    {
        return BingoTranslation.convertColors(name);
    }
}
