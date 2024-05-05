package io.github.steaf23.bingoreloaded.gui.base.item;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Bingo.MenuItem")
public record SlottedItem(int slot, ItemStack stack) implements ConfigurationSerializable
{
    @NotNull
    public Map<String, Object> serialize() {
        return new HashMap<>()
        {{
            put("slot", slot);
            put("stack", stack);
        }};
    }

    public static SlottedItem deserialize(Map<String, Object> data) {
        ItemStack stack = (ItemStack) data.get("stack");
        int slot = (int) data.get("slot");
        return new SlottedItem(slot, stack);
    }
}