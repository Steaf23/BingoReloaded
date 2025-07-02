package io.github.steaf23.bingoreloaded.lib.data.core.tag.adapters;

import io.github.steaf23.bingoreloaded.lib.data.core.tag.Tag;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagAdapter;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDTagAdapter implements TagAdapter<UUID, int[]>
{
    @Override
    public TagDataType<int[]> getBaseType() {
        return TagDataType.INT_ARRAY;
    }

    @Override
    public @NotNull UUID fromTag(Tag<int[]> tag) {
        int[] data = tag.getValue();
        if (data.length != 4) {
            ConsoleMessenger.bug("Could not parse UUID from file", this);
            return UUID.randomUUID();
        }

        long mostSig = (long)data[0] << 32 | data[1] & 0xFFFFFFFFL;
        long leastSig = (long)data[2] << 32 | data[3] & 0xFFFFFFFFL;

        return new UUID(mostSig, leastSig);
    }

    @Override
    public @NotNull Tag<int[]> toTag(UUID value) {
        int[] data = new int[4];

        long mostSig = value.getMostSignificantBits();
        data[0] = (int)(mostSig >> 32);
        data[1] = (int)mostSig;

        long leastSig = value.getLeastSignificantBits();
        data[2] = (int)(leastSig >> 32);
        data[3] = (int)leastSig;

        return new Tag.IntegerArrayTag(data);
    }

}
