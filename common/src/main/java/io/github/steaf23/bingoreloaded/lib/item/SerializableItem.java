package io.github.steaf23.bingoreloaded.lib.item;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import org.jetbrains.annotations.NotNull;


public record SerializableItem(int slot, @NotNull StackHandle stack)
{
    public static SerializableItem fromItemTemplate(ItemTemplate template) {
        return new SerializableItem(template.getSlot(), template.buildItem());
    }
}