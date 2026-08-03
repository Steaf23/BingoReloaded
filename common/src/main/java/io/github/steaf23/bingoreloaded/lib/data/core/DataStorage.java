package io.github.steaf23.bingoreloaded.lib.data.core;

import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagAdapter;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DataStorage
{
    DataStorage createNew();
    DataStorage duplicate();

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

    void setList(String path, List<DataStorage> values);
    List<DataStorage> getList(String path);

    <T> void setList(String path, TagDataType<T> type, List<T> values);
    <T> List<T> getList(String path, TagDataType<T> dataType);

    <T> void setList(String path, TagAdapter<T, ?> adapterType, List<T> values);
    <T> List<T> getList(String path, TagAdapter<T, ?> adapterType);

    <T> void setSerializableList(String path, DataStorageSerializer<T> serializer, List<T> values);
    <T> List<T> getSerializableList(String path, DataStorageSerializer<T> serializer);

    default <T> void setSerializable(String path, DataStorageSerializer<T> serializer, @NotNull T value) {
        DataStorage storage = createNew();
        storage.fromSerializable(serializer, value);
        setStorage(path, storage);
    }

    default <T> void fromSerializable(DataStorageSerializer<T> serializer, @NotNull T value) {
        if (serializer == null) {
            ConsoleMessenger.bug("No serializer registered for serialization " + serializer, this);
            return;
        }
        serializer.toDataStorage(this, value);
    }

    default <T> @Nullable T getSerializable(String path, DataStorageSerializer<T> serializer) {
        return getSerializable(path, serializer, null);
    }

    default <T> @NotNull T getSerializable(String path, DataStorageSerializer<T> serializer, T def) {
        DataStorage serializable = getStorage(path);
        if (serializable == null) {
            return def;
        }

        T value = serializable.toSerializable(serializer);
        return value == null ? def : value;
    }

    default <T> @Nullable T toSerializable(DataStorageSerializer<T> serializer) {
        if (serializer == null) {
            ConsoleMessenger.bug("No serializer registered for deserialization " + serializer, this);
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

    void setNamespacedKey(String path, @NotNull Key value);
    @NotNull Key getNamespacedKey(String path);

    void setStorage(String path, DataStorage value);
    @Nullable DataStorage getStorage(String path);

    default @NotNull DataStorage getStorageOrEmpty(String path) {
        DataStorage storage = getStorage(path);
        return storage == null ? createNew() : storage;
    }

    boolean isEmpty();

    /**
     * Also erases parent nodes of data node if they are empty after removal
     */
    void erase(String path);

    boolean contains(String path);
    void clear();
}
