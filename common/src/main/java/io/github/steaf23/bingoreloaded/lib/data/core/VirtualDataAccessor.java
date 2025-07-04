package io.github.steaf23.bingoreloaded.lib.data.core;

import io.github.steaf23.bingoreloaded.lib.PlayerDisplay;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagAdapter;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Data accessor that does not read or write its data from disc.
 * Can be used when a proper data accessor cannot be made/found.
 */
public class VirtualDataAccessor implements DataAccessor
{
    private final String filepath;

    public VirtualDataAccessor(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public String getLocation() {
        return filepath;
    }

    @Override
    public String getFileExtension() {
        return "";
    }

    @Override
    public void load() {
        ConsoleMessenger.bug("read filepath '" + filepath + "' and threw it in the void! (using empty node reader)", this);
    }

    @Override
    public void saveChanges() {
        ConsoleMessenger.bug("wanted to write node data to filepath '" + filepath + "', but threw it in the void! (using empty node writer)", this);
    }

    @Override
    public boolean isInternalReadOnly() {
        return false;
    }

    @Override
    public DataStorage createNew() {
        return null;
    }

    @Override
    public Set<String> getKeys() {
        return Set.of();
    }

    @Override
    public void setByte(String path, byte value) {

    }

    @Override
    public byte getByte(String path, byte def) {
        return def;
    }

    @Override
    public void setShort(String path, short value) {

    }

    @Override
    public short getShort(String path, short def) {
        return def;
    }

    @Override
    public void setInt(String path, int value) {

    }

    @Override
    public int getInt(String path, int def) {
        return def;
    }

    @Override
    public void setLong(String path, long value) {

    }

    @Override
    public long getLong(String path, long def) {
        return def;
    }

    @Override
    public void setString(String path, @NotNull String value) {

    }

    @Override
    public @NotNull String getString(String path, String def) {
        return def;
    }

    @Override
    public <T> void setList(String path, TagDataType<T> type, List<T> values) {

    }

    @Override
    public <T> List<T> getList(String path, TagDataType<T> dataType) {
        return List.of();
    }

    @Override
    public <T> void setList(String path, TagAdapter<T, ?> adapterType, List<T> values) {

    }

    @Override
    public <T> List<T> getList(String path, TagAdapter<T, ?> adapterType) {
        return List.of();
    }

    @Override
    public <T> void setSerializableList(String path, Class<T> dataType, List<T> values) {

    }

    @Override
    public <T> List<T> getSerializableList(String path, Class<T> dataType) {
        return List.of();
    }

    @Override
    public <T> void setSerializable(String path, Class<T> classType, T value) {

    }

    @Override
    public <T> @Nullable T getSerializable(String path, Class<T> classType) {
        return null;
    }

    @Override
    public <T> @NotNull T getSerializable(String path, Class<T> classType, T def) {
        return def;
    }

    @Override
    public void setBoolean(String path, boolean value) {

    }

    @Override
    public boolean getBoolean(String path) {
        return false;
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return def;
    }

    @Override
    public void setFloat(String path, float value) {

    }

    @Override
    public float getFloat(String path, float def) {
        return def;
    }

    @Override
    public void setDouble(String path, double value) {

    }

    @Override
    public double getDouble(String path, double def) {
        return def;
    }

    @Override
    public void setItemStack(String path, ItemStack value) {

    }

    @Override
    public @NotNull ItemStack getItemStack(String path) {
        return new ItemStack(Material.AIR);
    }

    @Override
    public void setUUID(String path, UUID value) {

    }

    @Override
    public @Nullable UUID getUUID(String path) {
        return null;
    }

    @Override
    public void setLocation(String path, @NotNull Location value) {

    }

    @Override
    public @Nullable Location getWorldPosition(String path) {
        return null;
    }

    public @NotNull Location getWorldPosition(String path, @NotNull Location def) {
        return def;
    }

    @Override
    public void setNamespacedKey(String path, @NotNull NamespacedKey value) {

    }

    @Override
    public @NotNull NamespacedKey getNamespacedKey(String path) {
        return new NamespacedKey(PlayerDisplay.getPlugin(), "unimplemented");
    }

    @Override
    public void setStorage(String path, DataStorage value) {

    }

    @Override
    public @Nullable DataStorage getStorage(String path) {
        return null;
    }

    @Override
    public void erase(String path) {

    }

    @Override
    public boolean contains(String path) {
        return false;
    }

    @Override
    public void clear() {

    }
}
