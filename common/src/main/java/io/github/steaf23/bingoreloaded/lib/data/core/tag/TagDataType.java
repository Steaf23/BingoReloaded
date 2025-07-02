package io.github.steaf23.bingoreloaded.lib.data.core.tag;

import io.github.steaf23.bingoreloaded.lib.data.core.tag.adapters.BooleanTagAdapter;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.adapters.ItemStackTagAdapter;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.adapters.UUIDTagAdapter;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TagDataType<T>
{
    private final int id; //practically always a byte, but this way less casting is required.
    private final Function<T, Tag<T>> tagWrapper;
    private final BiConsumer<T, ByteArrayOutputStream> payloadWriter;
    private final Function<ByteArrayInputStream, T> payloadReader;

    private TagDataType(int id, BiConsumer<T, ByteArrayOutputStream> writer, Function<ByteArrayInputStream, T> reader, Function<T, Tag<T>> wrapper) {
        this.id = id;
        this.payloadWriter = writer;
        this.payloadReader = reader;
        this.tagWrapper = wrapper;
    }

    public void writeBytes(T value, ByteArrayOutputStream stream) {
        payloadWriter.accept(value, stream);
    }

    public T readBytes(ByteArrayInputStream stream) {
        return payloadReader.apply(stream);
    }

    public int getId() {
        return id;
    }

    public Tag<T> createTag(T value) {
        return tagWrapper.apply(value);
    }

    public Tag<T> createTagFromStream(ByteArrayInputStream stream) {
        return createTag(readBytes(stream));
    }

    public static final TagDataType<Byte> BYTE = registerTagType(1,
            (value, stream) -> stream.write((int) value),
            stream -> (byte) stream.read(),
            Tag.ByteTag::new);

    public static final TagDataType<Short> SHORT = registerTagType(2,
            ByteHelper::writeShort,
            ByteHelper::readShort,
            Tag.ShortTag::new);

    public static final TagDataType<Integer> INT = registerTagType(3,
            ByteHelper::writeInt,
            ByteHelper::readInt,
            Tag.IntegerTag::new);

    public static final TagDataType<Long> LONG = registerTagType(4,
            ByteHelper::writeLong,
            ByteHelper::readLong,
            Tag.LongTag::new);

    public static final TagDataType<Float> FLOAT = registerTagType(5,
            ByteHelper::writeFloat,
            ByteHelper::readFloat,
            Tag.FloatTag::new);

    public static final TagDataType<Double> DOUBLE = registerTagType(6,
            ByteHelper::writeDouble,
            ByteHelper::readDouble,
            Tag.DoubleTag::new);

    public static final TagDataType<byte[]> BYTE_ARRAY = registerTagType(7,
            (value, stream) ->
            {
                ByteHelper.writeInt(value.length, stream);
                stream.writeBytes(value);
            },
            stream ->
            {
                int size = ByteHelper.readInt(stream);
                byte[] bytes = new byte[size];
                stream.readNBytes(bytes, 0, size);
                return bytes;
            },
            Tag.ByteArrayTag::new);

    public static final TagDataType<String> STRING = registerTagType(8,
            ByteHelper::writeString,
            ByteHelper::readString,
            Tag.StringTag::new);

    public static final TagDataType<TagList> LIST = registerTagType(9,
            TagList::getPayload,
            TagList::fromPayload,
            Tag.ListTag::new);

    public static final TagDataType<TagTree> COMPOUND = registerTagType(10,
            TagTree::getPayload,
            TagTree::fromPayload,
            Tag.CompoundTag::new);

    public static final TagDataType<int[]> INT_ARRAY = registerTagType(11,
            (values, stream) ->
            {
                ByteHelper.writeInt(values.length, stream);
                for (int v : values) {
                    ByteHelper.writeInt(v, stream);
                }
            },
            stream ->
            {
                int size = ByteHelper.readInt(stream);
                int[] result = new int[size];
                for (int i = 0; i < size; i++) {
                    result[i] = ByteHelper.readInt(stream);
                }

                return result;
            },
            Tag.IntegerArrayTag::new);
    public static final TagDataType<long[]> LONG_ARRAY = registerTagType(12,
            (value, stream) ->
            {
                ByteHelper.writeInt(value.length, stream);
                for (long v : value) {
                    ByteHelper.writeLong(v, stream);
                }
            },
            stream ->
            {
                int size = ByteHelper.readInt(stream);
                long[] result = new long[size];
                for (int i = 0; i < size; i++) {
                    result[i] = ByteHelper.readLong(stream);
                }
                return result;
            },
            Tag.LongArrayTag::new);

    public static final TagAdapter<Boolean, Byte> BOOLEAN = new BooleanTagAdapter();
    public static final TagAdapter<java.util.UUID, int[]> UUID = new UUIDTagAdapter();
    public static final TagAdapter<ItemStack, byte[]> ITEM_STACK = new ItemStackTagAdapter();

    private static Map<Integer, TagDataType<?>> types;

    private static <U> TagDataType<U> registerTagType(int id, BiConsumer<U, ByteArrayOutputStream> streamWriter, Function<ByteArrayInputStream, U> streamReader, Function<U, Tag<U>> wrapper) {
        TagDataType<U> type = new TagDataType<>(id, streamWriter, streamReader, wrapper);
        if (types == null) {
            // lazily initialize type map (even though its like 99% sure we will create at least 1 type anyway...)
            types = new HashMap<>();
        }
        types.put(id, type);
        return type;
    }

    public static @Nullable TagDataType<?> getTypeFromId(int id) {
        return types.get(id);
    }
}
