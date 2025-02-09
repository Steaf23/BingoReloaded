package io.github.steaf23.bingoreloaded.data.core.tag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used to register more complex type of tag data that can be used to store any data that can be saved in a Tag.
 * Adapted tags are stored as their base type, and can then be retrieved either using the adapter for automatic conversion
 * or using the base tag type.
 * @param <T> Type of data to adapt.
 * @param <Base> base nbt type that this data will be stored as.
 */
public interface TagAdapter<T, Base>
{
    TagDataType<Base> getBaseType();

    @NotNull
    T fromTag(Tag<Base> tag);

    @NotNull
    Tag<Base> toTag(T value);

    default T fromTagOrNull(@Nullable Tag<?> tag) {
        if (tag == null) return null;

        if (tag.getType() == getBaseType())
        {
            return fromTag((Tag<Base>)tag);
        }
        return null;
    }
}

