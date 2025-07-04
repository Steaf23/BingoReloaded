package io.github.steaf23.bingoreloaded.gui.inventory.item;

import io.github.steaf23.bingoreloaded.lib.api.StackHandle;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.jetbrains.annotations.NotNull;


public record SerializableItem(int slot, @NotNull StackHandle stack)
{
    public static SerializableItem fromItemTemplate(ItemTemplate template) {
        return new SerializableItem(template.getSlot(), template.buildItem(false));
    }
}