package io.github.steaf23.bingoreloaded.data.core;

import io.github.steaf23.bingoreloaded.data.core.tag.TagAdapter;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataType;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DataStorage
{
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

    <T> void setSerializable(String path, Class<T> classType, T value);
    <T> @Nullable T getSerializable(String path, Class<T> classType);
    <T> @NotNull T getSerializable(String path, Class<T> classType, T def);

    void setBoolean(String path, boolean value);
    boolean getBoolean(String path);
    boolean getBoolean(String path, boolean def);

    void setFloat(String path, float value);
    float getFloat(String path, float def);

    void setDouble(String path, double value);
    double getDouble(String path, double def);

    void setItemStack(String path, ItemStack value);
    @NotNull ItemStack getItemStack(String path);

    void setUUID(String path, UUID value);
    @Nullable UUID getUUID(String path);

    void setLocation(String path, @NotNull Location value);
    @Nullable Location getLocation(String path);
    @NotNull Location getLocation(String path, @NotNull Location def);

    void setNamespacedKey(String path, @NotNull NamespacedKey value);
    @NotNull NamespacedKey getNamespacedKey(String path);

    void setStorage(String path, DataStorage value);
    @Nullable DataStorage getStorage(String path);

    /**
     * Also erases parent nodes of data node if they are empty after removal
     */
    void erase(String path);

    boolean contains(String path);
    void clear();
}
