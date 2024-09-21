package io.github.steaf23.bingoreloaded.gui.inventory.item;

import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import org.bukkit.inventory.ItemStack;


public record SerializableItem(int slot, ItemStack stack)
{
    public static SerializableItem fromItemTemplate(ItemTemplate template) {
        return new SerializableItem(template.getSlot(), template.buildItem(false));
    }
}