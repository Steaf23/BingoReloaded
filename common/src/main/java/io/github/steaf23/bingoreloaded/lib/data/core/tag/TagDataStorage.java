package io.github.steaf23.bingoreloaded.lib.data.core.tag;

import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializerRegistry;
import io.github.steaf23.bingoreloaded.lib.data.core.node.NodeLikeData;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TagDataStorage implements DataStorage
{
    private Tag.CompoundTag root;

    public TagDataStorage() {
        this(new Tag.CompoundTag(new TagTree()));
    }

    public TagDataStorage(Tag.CompoundTag rootTag) {
        root = rootTag;
    }

    public TagDataStorage(TagTree tree) {
        root = new Tag.CompoundTag(tree);
    }

    @Override
    public TagDataStorage createNew() {
        return new TagDataStorage();
    }

    @Override
    public Set<String> getKeys() {
        return root.getValue().getKeys();
    }

    @Override
    public void setByte(String path, byte value) {
        set(path, TagDataType.BYTE.createTag(value));
    }

    @Override
    public byte getByte(String path, byte def) {
        Tag<?> tag = get(path);
        if (tag != null && tag.getType() == TagDataType.BYTE) {
            return (byte) tag.getValue();
        }
        return def;
    }

    @Override
    public void setShort(String path, short value) {
        set(path, TagDataType.SHORT.createTag(value));
    }

    @Override
    public short getShort(String path, short def) {
        Tag<?> tag = get(path);
        if (tag != null && tag.getType() == TagDataType.SHORT) {
            return (short) tag.getValue();
        }
        return def;
    }

    @Override
    public void setInt(String path, int value) {
        set(path, new Tag.IntegerTag(value));
    }

    @Override
    public int getInt(String path, int def) {
        Tag<?> tag = get(path);
        if (tag != null && tag.getType() == TagDataType.INT) {
            return (int) tag.getValue();
        }
        return def;
    }

    @Override
    public void setLong(String path, long value) {
        set(path, TagDataType.LONG.createTag(value));
    }

    @Override
    public long getLong(String path, long def) {
        Tag<?> tag = get(path);
        if (tag != null && tag.getType() == TagDataType.LONG) {
            return (long) tag.getValue();
        }
        return def;
    }

    @Override
    public void setString(String path, @NotNull String value) {
        set(path, TagDataType.STRING.createTag(value));
    }

    @Override
    public @NotNull String getString(String path, String def) {
        Tag<?> tag = get(path);
        if (tag != null && tag.getType() == TagDataType.STRING) {
            return (String) tag.getValue();
        }
        return def;
    }

    @Override
    public <T> void setList(String path, TagDataType<T> type, List<T> values) {
        if (type == TagDataType.BYTE) {
            byte[] bytes = new byte[values.size()];
            int i = 0;
            for (T value : values) {
                bytes[i] = (byte) value;
                i++;
            }
            set(path, new Tag.ByteArrayTag(bytes));
            return;
        }
        if (type == TagDataType.INT) {
            int[] ints = new int[values.size()];
            int i = 0;
            for (T value : values) {
                ints[i] = (int) value;
                i++;
            }
            set(path, new Tag.IntegerArrayTag(ints));
            return;
        }
        if (type == TagDataType.LONG) {
            long[] longs = new long[values.size()];
            int i = 0;
            for (T value : values) {
                longs[i] = (long) value;
                i++;
            }
            set(path, new Tag.LongArrayTag(longs));
            return;
        }

        TagList tags = new TagList();
        for (T v : values) {
            tags.addTag(type.createTag(v));
        }

        set(path, TagDataType.LIST.createTag(tags));
    }

    @Override
    public <T> List<T> getList(String path, TagDataType<T> dataType) {
        Tag<?> tag = get(path);
        if (tag == null) {
            return List.of();
        }
        if (tag.getType() == TagDataType.LIST) {
            TagList tagList = (TagList) tag.getValue();
            return tagList.getList(dataType);
        } else if (tag.getType() == TagDataType.BYTE_ARRAY) {
            if (tag instanceof Tag.ByteArrayTag arrTag) {
                List<T> result = new ArrayList<>();
                for (Byte t : arrTag.getValue()) {
                    T v = (T) t;
                    result.add(v);
                }
                return result;
            }
            return List.of();
        } else if (tag.getType() == TagDataType.INT_ARRAY) {
            if (tag instanceof Tag.IntegerArrayTag arrTag) {
                List<T> result = new ArrayList<>();
                for (Integer t : arrTag.getValue()) {
                    T v = (T) t;
                    result.add(v);
                }
                return result;
            }
            return List.of();
        } else if (tag.getType() == TagDataType.LONG_ARRAY) {
            if (tag instanceof Tag.LongArrayTag arrTag) {
                List<T> result = new ArrayList<>();
                for (Long t : arrTag.getValue()) {
                    T v = (T) t;
                    result.add(v);
                }
                return result;
            }
            return List.of();
        }
        return List.of();
    }

    @Override
    public <T> void setList(String path, TagAdapter<T, ?> adapterType, List<T> values) {
        //FIXME: Currently byte-, int- and long- array adapters will not work because of the casting happening here...
        if (adapterType.getBaseType() == TagDataType.BYTE) {
            byte[] bytes = new byte[values.size()];
            int i = 0;
            for (T value : values) {
                bytes[i] = (byte) adapterType.toTag(value).getValue();
                i++;
            }
            set(path, TagDataType.BYTE_ARRAY.createTag(bytes));
            return;
        }
        if (adapterType.getBaseType() == TagDataType.INT) {
            int[] ints = new int[values.size()];
            int i = 0;
            for (T value : values) {
                ints[i] = (int) adapterType.toTag(value).getValue();
                i++;
            }
            set(path, TagDataType.INT_ARRAY.createTag(ints));
            return;
        }
        if (adapterType.getBaseType() == TagDataType.LONG) {
            long[] longs = new long[values.size()];
            int i = 0;
            for (T value : values) {
                longs[i] = (int) adapterType.toTag(value).getValue();
                i++;
            }
            set(path, TagDataType.LONG_ARRAY.createTag(longs));
            return;
        }

        TagList tags = new TagList();
        for (T v : values) {
            tags.addTag(adapterType.toTag(v));
        }

        set(path, TagDataType.LIST.createTag(tags));
    }

    @Override
    public <T> List<T> getList(String path, TagAdapter<T, ?> adapterType) {
        Tag<?> tag = get(path);
        if (tag == null) {
            return List.of();
        }
        if (tag.getType() == TagDataType.LIST) {
            TagList tagList = (TagList) tag.getValue();
            return tagList.getTags().stream().map(adapterType::fromTagOrNull).toList();
        } else if (tag.getType() == TagDataType.BYTE_ARRAY) {
            byte[] values = (byte[]) tag.getValue();
            List<T> result = new ArrayList<>();
            for (byte v : values) {
                result.add(adapterType.fromTagOrNull(new Tag.ByteTag(v)));
            }
            return result;
        } else if (tag.getType() == TagDataType.INT_ARRAY) {
            int[] values = (int[]) tag.getValue();
            List<T> result = new ArrayList<>();
            for (int v : values) {
                result.add(adapterType.fromTagOrNull(new Tag.IntegerTag(v)));
            }
            return result;
        } else if (tag.getType() == TagDataType.LONG_ARRAY) {
            long[] values = (long[]) tag.getValue();
            List<T> result = new ArrayList<>();
            for (long v : values) {
                result.add(adapterType.fromTagOrNull(new Tag.LongTag(v)));
            }
            return result;
        }
        return List.of();
    }

    @Override
    public <T> void setSerializableList(String path, Class<T> classType, List<T> values) {
        setList(path, TagDataType.COMPOUND, values.stream()
                .map(v -> {
                    TagDataStorage storage = new TagDataStorage();
                    DataStorageSerializer<T> serializer = DataStorageSerializerRegistry.getSerializer(classType);
                    if (serializer == null) {
                        return storage.root.getValue();
                    }
                    serializer.toDataStorage(storage, v);
                    return storage.root.getValue();
                })
                .toList());
    }

    @Override
    public <T> List<T> getSerializableList(String path, Class<T> classType) {
        DataStorageSerializer<T> serializer = DataStorageSerializerRegistry.getSerializer(classType);
        if (serializer == null) {
            ConsoleMessenger.bug("No serializer registered for this type of data at path " + path, this);
            return List.of();
        }

        return getList(path, TagDataType.COMPOUND).stream()
                .map(v -> serializer.fromDataStorage(new TagDataStorage(new Tag.CompoundTag(v))))
                .toList();
    }

    @Override
    public void setBoolean(String path, boolean value) {
        set(path, TagDataType.BOOLEAN.toTag(value));
    }

    @Override
    public boolean getBoolean(String path) {
        Boolean bool = TagDataType.BOOLEAN.fromTagOrNull(get(path));
        return bool != null ? bool : false;
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        Boolean bool = TagDataType.BOOLEAN.fromTagOrNull(get(path));
        return bool != null ? bool : def;
    }

    @Override
    public void setFloat(String path, float value) {
        set(path, TagDataType.FLOAT.createTag(value));
    }

    @Override
    public float getFloat(String path, float def) {
        Tag<?> tag = get(path);
        if (tag != null && tag.getType() == TagDataType.FLOAT) {
            return (float) tag.getValue();
        }
        return def;
    }

    @Override
    public void setDouble(String path, double value) {
        set(path, TagDataType.DOUBLE.createTag(value));
    }

    @Override
    public double getDouble(String path, double def) {
        Tag<?> tag = get(path);
        if (tag != null && tag.getType() == TagDataType.DOUBLE) {
            return (double) tag.getValue();
        }
        return def;
    }

    @Override
    public void setItemStack(String path, StackHandle value) {
        set(path, TagDataType.ITEM_STACK.toTag(value));
    }

    @Override
    public @NotNull StackHandle getItemStack(String path) {
        StackHandle stack = TagDataType.ITEM_STACK.fromTagOrNull(get(path));
        return stack == null ? StackHandle.create(ItemType.AIR) : stack;
    }

    @Override
    public void setUUID(String path, @Nullable UUID value) {
        if (value == null) {
            return;
        }

        set(path, TagDataType.UUID.toTag(value));
    }

    @Override
    public @Nullable UUID getUUID(String path) {
        return TagDataType.UUID.fromTagOrNull(get(path));
    }

    @Override
    public void setWorldPosition(String path, @NotNull WorldPosition value) {
        setSerializable(path, WorldPosition.class, value);
    }

    @Override
    public @Nullable WorldPosition getWorldPosition(String path) {
        return getSerializable(path, WorldPosition.class);
    }

    public @NotNull WorldPosition getWorldPosition(String path, @NotNull WorldPosition def) {
        WorldPosition loc = getSerializable(path, WorldPosition.class);
        return loc == null ? def : loc;
    }

    @Override
    public void setNamespacedKey(String path, @NotNull Key value) {
        setString(path, value.toString());
    }

    @Override
    public @NotNull Key getNamespacedKey(String path) {
        @Subst("minecraft:duck") String stringified = getString(path, "");
        if (!stringified.isEmpty()) {
            return Key.key(stringified);
        }
        ConsoleMessenger.bug("Could not read namespaced key: '" + stringified + "'", this.getClass());
        assert(false);
        return Key.key("", "");
    }

    @Override
    public void setStorage(String path, DataStorage value) {
        if (!(value instanceof TagDataStorage tagStorage)) {
            ConsoleMessenger.bug("Cannot add data storage to tag storage, because it is of a different type!", this);
            return;
        }
        set(path, tagStorage.root);
    }

    @Override
    public @Nullable DataStorage getStorage(String path) {
        Tag<?> tag = get(path);
        if (tag == null) {
            return null;
        }
        if (tag.getType() != TagDataType.COMPOUND) {
            return null;
        }
        return new TagDataStorage((Tag.CompoundTag) tag);
    }

    @Override
    public void erase(String path) {
        NodeLikeData.removeNested(root, path);
    }

    @Override
    public boolean contains(String path) {
        return NodeLikeData.containsFullPath(root, path);
    }

    @Override
    public void clear() {
        root = new Tag.CompoundTag(new TagTree());
    }

    public TagTree getTree() {
        return root.getValue();
    }

    public void setTree(TagTree tree) {
        root = new Tag.CompoundTag(tree);
    }

    private @Nullable Tag<?> get(String path) {
        return NodeLikeData.getNested(root, path);
    }

    private void set(String key, Tag<?> tag) {
        NodeLikeData.setNested(root, key, tag, () -> new Tag.CompoundTag(new TagTree()));
    }
}
