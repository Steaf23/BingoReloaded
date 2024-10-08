package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record CustomKit(Component name, PlayerKit slot, List<SerializableItem> items, int cardSlot)
{
    public static CustomKit fromPlayerInventory(Player player, Component kitName, PlayerKit kitSlot)
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
}
