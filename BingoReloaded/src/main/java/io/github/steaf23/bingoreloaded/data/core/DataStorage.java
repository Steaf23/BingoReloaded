package io.github.steaf23.bingoreloaded.data.core;

import io.github.steaf23.bingoreloaded.data.core.node.NodeSerializer;
import io.github.steaf23.bingoreloaded.data.core.node.datatype.NodeDataType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DataStorage<T extends DataStorage<?>>
{
    Set<String> getKeys();

    void setString(String path, @NotNull String value);
    String getString(String path);
    String getString(String path, String def);

    <T> void setList(String path, NodeDataType<T> type, List<T> values);
    <T extends NodeSerializer> void setList(String path, List<T> values);

    <T> List<T> getList(String path, NodeDataType<T> dataType);
    <T extends NodeSerializer> List<T> getList(String path, Class<T> classType);

    void setSerializable(String path, NodeSerializer value);
    <T extends NodeSerializer> T getSerializable(String path, Class<T> classType);
    <T extends NodeSerializer> T getSerializable(String path, Class<T> classType, T def);

    void setBoolean(String path, boolean value);
    boolean getBoolean(String path);
    boolean getBoolean(String path, Boolean def);

    void setInt(String path, int value);
    int getInt(String path);
    int getInt(String path, int def);

    void setDouble(String path, double value);
    double getDouble(String path);
    double getDouble(String path, double def);

    void setBytes(String path, byte[] value);
    byte[] getBytes(String path);

    void setItemStack(String path, ItemStack value);
    @NotNull ItemStack getItemStack(String path);

    void setUUID(String path, UUID value);
    @Nullable UUID getUUID(String path);

    void setLocation(String path, Location value);
    @Nullable Location getLocation(String path);
    @NotNull Location getLocation(String path, @NotNull Location def);

    void setStorage(String path, T value);
    @Nullable T getStorage(String path);

    /**
     * Also erases parent nodes of data node if they are empty after removal
     */
    void erase(String path);

    boolean contains(String path);
}
