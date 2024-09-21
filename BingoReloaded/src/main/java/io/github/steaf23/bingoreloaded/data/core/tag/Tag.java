package io.github.steaf23.bingoreloaded.data.core.tag;

import io.github.steaf23.bingoreloaded.data.core.node.NodeLikeData;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;

public interface Tag<T> extends NodeLikeData.Node
{
    TagDataType<T> getType();

    T getValue();

    default void writePayloadBytes(ByteArrayOutputStream stream) {
        getType().writeBytes(getValue(), stream);
    }

    record ByteTag(byte value) implements Tag<Byte>
    {
        @Override
        public TagDataType<Byte> getType() {
            return TagDataType.BYTE;
        }

        @Override
        public Byte getValue() {
            return value;
        }
    }

    record ShortTag(short value) implements Tag<Short>
    {
        @Override
        public TagDataType<Short> getType() {
            return TagDataType.SHORT;
        }

        @Override
        public Short getValue() {
            return value;
        }
    }

    record IntegerTag(int value) implements Tag<Integer>
    {
        @Override
        public TagDataType<Integer> getType() {
            return TagDataType.INT;
        }

        @Override
        public Integer getValue() {
            return value;
        }
    }

    record LongTag(long value) implements Tag<Long>
    {
        @Override
        public TagDataType<Long> getType() {
            return TagDataType.LONG;
        }

        @Override
        public Long getValue() {
            return value;
        }
    }

    record FloatTag(float value) implements Tag<Float>
    {
        @Override
        public TagDataType<Float> getType() {
            return TagDataType.FLOAT;
        }

        @Override
        public Float getValue() {
            return value;
        }
    }

    record DoubleTag(double value) implements Tag<Double>
    {
        @Override
        public TagDataType<Double> getType() {
            return TagDataType.DOUBLE;
        }

        @Override
        public Double getValue() {
            return value;
        }
    }

    record StringTag(String value) implements Tag<String>
    {
        @Override
        public TagDataType<String> getType() {
            return TagDataType.STRING;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    record ListTag(TagList data) implements Tag<TagList> {

        @Override
        public TagDataType<TagList> getType() {
            return TagDataType.LIST;
        }

        @Override
        public TagList getValue() {
            return data;
        }
    }

    record CompoundTag(TagTree data) implements Tag<TagTree>, NodeLikeData.NodeBranch<Tag<?>>
    {
        @Override
        public TagDataType<TagTree> getType() {
            return TagDataType.COMPOUND;
        }

        @Override
        public TagTree getValue() {
            return data;
        }

        @Override
        public @Nullable Tag<?> getData(String path) {
            return data.getChild(path);
        }

        @Override
        public void putData(String path, @Nullable Tag<?> newData) {
            data.putChild(path, newData);
        }

        @Override
        public void removeData(String path) {
            data.removeChild(path);
        }

        @Override
        public boolean contains(String path) {
            return data.containsChild(path);
        }

        @Override
        public boolean isEmpty() {
            return data.isEmpty();
        }
    }

    record ByteArrayTag(byte[] data) implements Tag<byte[]>
    {
        @Override
        public TagDataType<byte[]> getType() {
            return TagDataType.BYTE_ARRAY;
        }

        @Override
        public byte[] getValue() {
            return data;
        }
    }

    record IntegerArrayTag(int[] data) implements Tag<int[]>
    {
        @Override
        public TagDataType<int[]> getType() {
            return TagDataType.INT_ARRAY;
        }

        @Override
        public int[] getValue() {
            return data;
        }
    }

    record LongArrayTag(long[] data) implements Tag<long[]>
    {
        @Override
        public TagDataType<long[]> getType() {
            return TagDataType.LONG_ARRAY;
        }

        @Override
        public long[] getValue() {
            return data;
        }
    }

}
