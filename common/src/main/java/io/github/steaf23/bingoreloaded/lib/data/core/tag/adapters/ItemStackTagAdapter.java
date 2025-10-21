package io.github.steaf23.bingoreloaded.lib.data.core.tag.adapters;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.Tag;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagAdapter;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import org.jetbrains.annotations.NotNull;

public class ItemStackTagAdapter implements TagAdapter<StackHandle, byte[]>
{
    @Override
    public TagDataType<byte[]> getBaseType() {
        return TagDataType.BYTE_ARRAY;
    }

    @Override
    public @NotNull StackHandle fromTag(Tag<byte[]> tag) {
		if (tag.getValue().length == 0) {
			return StackHandle.create(ItemType.AIR);
		} else {
			return StackHandle.deserializeBytes(tag.getValue());
		}
    }

    @Override
    public @NotNull Tag<byte[]> toTag(@NotNull StackHandle value) {
		if (value.type().isAir()) {
			return new Tag.ByteArrayTag(new byte[]{});
		} else {
			return new Tag.ByteArrayTag(StackHandle.serializeBytes(value));
		}
    }
}
