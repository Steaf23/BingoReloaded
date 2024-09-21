package io.github.steaf23.bingoreloaded.data.core.tag.adapters;

import io.github.steaf23.bingoreloaded.data.core.tag.Tag;
import io.github.steaf23.bingoreloaded.data.core.tag.TagAdapter;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackTagAdapter implements TagAdapter<ItemStack, byte[]>
{
    @Override
    public TagDataType<byte[]> getBaseType() {
        return TagDataType.BYTE_ARRAY;
    }

    @Override
    public @NotNull ItemStack fromTag(Tag<byte[]> tag) {
        return ItemStack.deserializeBytes(tag.getValue());
    }

    @Override
    public @NotNull Tag<byte[]> toTag(ItemStack value) {
        return new Tag.ByteArrayTag(value.serializeAsBytes());
    }
}
