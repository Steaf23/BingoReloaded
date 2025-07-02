package io.github.steaf23.bingoreloaded.lib.data.core.configuration;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializerRegistry;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagAdapter;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class YamlDataStorage implements DataStorage
{
    protected ConfigurationSection config;

    public YamlDataStorage(ConfigurationSection config) {
        this.config = config;
    }

    public YamlDataStorage() {
        this.config = new YamlConfiguration();
    }

    @Override
    public DataStorage createNew() {
        return new YamlDataStorage();
    }

    @Override
    public Set<String> getKeys() {
        return config.getKeys(false);
    }

    @Override
    public void setByte(String path, byte value) {
        config.set(path, value);
    }

    @Override
    public byte getByte(String path, byte def) {
        return (byte)config.getInt(path, def);
    }

    @Override
    public void setShort(String path, short value) {
        config.set(path, value);
    }

    @Override
    public short getShort(String path, short def) {
        return (short)config.getInt(path, def);
    }

    @Override
    public void setInt(String path, int value) {
        config.set(path, value);
    }

    @Override
    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    @Override
    public void setLong(String path, long value) {
        config.set(path, value);
    }

    @Override
    public long getLong(String path, long def) {
        return config.getLong(path, def);
    }

    @Override
    public void setString(String path, @NotNull String value) {
        config.set(path, value);
    }

    @Override
    public @NotNull String getString(String path, String def) {
        return config.getString(path, def);
    }

    @Override
    public <T> void setList(String path, TagDataType<T> type, List<T> values) {
        config.set(path, values);
    }

    @Override
    public <T> List<T> getList(String path, TagDataType<T> dataType) {
        try {
            return (List<T>) config.getList(path);
        } catch (ClassCastException castException) {
            return List.of();
        }
    }

    @Override
    public <T> void setList(String path, TagAdapter<T, ?> adapterType, List<T> values) {
        config.set(path, values);
    }

    @Override
    public <T> List<T> getList(String path, TagAdapter<T, ?> adapterType) {
        return getList(path, (TagDataType<T>) null);
    }

    @Override
    public <T> void setSerializableList(String path, Class<T> dataType, List<T> values) {
        config.set(path, values.stream()
                .map(v -> {
                    YamlDataStorage storage = new YamlDataStorage();
                    DataStorageSerializer<T> serializer = DataStorageSerializerRegistry.getSerializer(dataType);
                    if (serializer == null) {
                        ConsoleMessenger.bug("No serializer registered for this type of data at path " + path, this);
                        return storage.config;
                    }
                    serializer.toDataStorage(storage, v);
                    return storage.config;
                })
                .toList());
    }

    @Override
    public <T> List<T> getSerializableList(String path, Class<T> dataType) {
        DataStorageSerializer<T> serializer = DataStorageSerializerRegistry.getSerializer(dataType);
        if (serializer == null) {
            ConsoleMessenger.bug("No serializer registered for this type of data at path " + path, this);
            return List.of();
        }

        return config.getList(path, List.of()).stream()
                .map(v -> serializer.fromDataStorage(new YamlDataStorage()))
                .toList();
    }

    @Override
    public void setBoolean(String path, boolean value) {
        config.set(path, value);
    }

    @Override
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    @Override
    public void setFloat(String path, float value) {
        config.set(path, value);
    }

    @Override
    public float getFloat(String path, float def) {
        return (float)config.getDouble(path, def);
    }

    @Override
    public void setDouble(String path, double value) {
        config.set(path, value);
    }

    @Override
    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    @Override
    public void setItemStack(String path, ItemStack value) {
        config.set(path, value);
    }

    @Override
    public @NotNull ItemStack getItemStack(String path) {
        return config.getItemStack(path, new ItemStack(Material.AIR));
    }

    @Override
    public void setUUID(String path, UUID value) {
        config.set(path, value.toString());
    }

    @Override
    public @Nullable UUID getUUID(String path) {
        String uuidString = config.getString(path, "");
        if (uuidString.isEmpty()) {
            return null;
        }
        return UUID.fromString(uuidString);
    }

    @Override
    public void setLocation(String path, @NotNull Location value) {
        config.set(path, value);
    }

    @Override
    public @Nullable Location getLocation(String path) {
        return config.getLocation(path);
    }

    @Override
    public @NotNull Location getLocation(String path, @NotNull Location def) {
        return config.getLocation(path, def);
    }

    @Override
    public void setNamespacedKey(String path, @NotNull NamespacedKey value) {
        config.set(path, value);
    }

    @Override
    public @NotNull NamespacedKey getNamespacedKey(String path) {
        Object ns = config.get(path);
        if (ns instanceof NamespacedKey key) {
            return key;
        }
        return new NamespacedKey("", "");
    }

    @Override
    public void setStorage(String path, DataStorage value) {
        if (!(value instanceof YamlDataStorage yamlDataStorage)) {
            ConsoleMessenger.bug("Cannot add data storage to file configuration storage, because it is of a different type!", this);
            return;
        }
        config.set(path, yamlDataStorage.config);
    }

    @Override
    public @Nullable DataStorage getStorage(String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return null;
        }
        return new YamlDataStorage(section);
    }

    @Override
    public void erase(String path) {
        config.set(path, null);
    }

    @Override
    public boolean contains(String path) {
        return config.contains(path);
    }

    @Override
    public void clear() {
        config = new YamlConfiguration();
    }

    public @NotNull ConfigurationSection getSection() {
        return config;
    }

    public void setComment(String forKey, List<String> comments) {
        if (!(config instanceof YamlConfiguration yamlConfig)) {
            return;
        }
        yamlConfig.setComments(forKey, comments);
    }
}
