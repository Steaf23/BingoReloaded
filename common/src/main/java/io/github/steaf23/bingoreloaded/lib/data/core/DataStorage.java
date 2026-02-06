package io.github.steaf23.bingoreloaded.lib.data.core;

import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagAdapter;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DataStorage
{
    DataStorage createNew();

    Set<String> getKeys();

    void setByte(String path, byte value);
    byte getByte(String path, byte def);

    void setShort(String path, short value);
    short getShort(String path, short def);

    void setInt(String path, int value);
    int getInt(String path, int def);

    void setLong(String path, long value);
    long getLong(String path, long def);

    void setString(String path, @NotNull String value);
    @NotNull String getString(String path, String def);

    <T> void setList(String path, TagDataType<T> type, List<T> values);
    <T> List<T> getList(String path, TagDataType<T> dataType);

    <T> void setList(String path, TagAdapter<T, ?> adapterType, List<T> values);
    <T> List<T> getList(String path, TagAdapter<T, ?> adapterType);

    <T> void setSerializableList(String path, Class<T> dataType, List<T> values);
    <T> List<T> getSerializableList(String path, Class<T> dataType);

    default <T> void setSerializable(String path, Class<T> classType, @NotNull T value) {
        DataStorage storage = createNew();
        storage.fromSerializable(classType, value);
        setStorage(path, storage);
    }

    default <T> void fromSerializable(Class<T> classType, @NotNull T value) {
        DataStorageSerializer<T> serializer = DataStorageSerializerRegistry.getSerializer(classType);
        if (serializer == null) {
            ConsoleMessenger.bug("No serializer registered for serialization " + classType, this);
            return;
        }
        serializer.toDataStorage(this, value);
    }

    default <T> @Nullable T getSerializable(String path, Class<T> classType) {
        return getSerializable(path, classType, null);
    }

    default <T> @NotNull T getSerializable(String path, Class<T> classType, T def) {
        DataStorage serializable = getStorage(path);
        if (serializable == null) {
            return def;
        }

        T value = serializable.toSerializable(classType);
        return value == null ? def : value;
    }

    default <T> @Nullable T toSerializable(Class<T> classType) {
        DataStorageSerializer<T> serializer = DataStorageSerializerRegistry.getSerializer(classType);
        if (serializer == null) {
            ConsoleMessenger.bug("No serializer registered for deserialization " + classType, this);
            return null;
        }
        return serializer.fromDataStorage(this);
    }

    void setBoolean(String path, boolean value);
    boolean getBoolean(String path);
    boolean getBoolean(String path, boolean def);

    void setFloat(String path, float value);
    float getFloat(String path, float def);

    void setDouble(String path, double value);
    double getDouble(String path, double def);

    void setItemStack(String path, StackHandle value);
    @NotNull StackHandle getItemStack(String path);

    void setUUID(String path, @Nullable UUID value);
    @Nullable UUID getUUID(String path);

    void setWorldPosition(String path, @NotNull WorldPosition value);
    @Nullable WorldPosition getWorldPosition(String path);
    @NotNull WorldPosition getWorldPosition(String path, @NotNull WorldPosition def);

    default void setItemType(String path, @NotNull ItemType value) {
        PlatformResolver.get().writeItemType(this, path, value);
    }

    default @NotNull ItemType getItemType(String path) {
        return PlatformResolver.get().readItemType(this, path);
    }

    void setStorage(String path, DataStorage value);
    @Nullable DataStorage getStorage(String path);

    /**
     * Also erases parent nodes of data node if they are empty after removal
     */
    void erase(String path);

    boolean contains(String path);
    void clear();
}
