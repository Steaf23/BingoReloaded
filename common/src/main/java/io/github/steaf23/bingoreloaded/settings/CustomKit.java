package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import io.github.steaf23.bingoreloaded.lib.api.StackHandle;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public record CustomKit(Component name, PlayerKit slot, List<SerializableItem> items, int cardSlot)
{
    public static CustomKit fromPlayerInventory(PlayerHandle player, Component kitName, PlayerKit kitSlot)
    {
        List<SerializableItem> items = new ArrayList<>();
        int slot = 0;
        int cardSlot = 40;
        for (StackHandle itemStack : player.inventory().contents())
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
