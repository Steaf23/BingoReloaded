package io.github.steaf23.bingoreloaded.data.core;

import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.bingoreloaded.data.core.node.NodeSerializer;
import io.github.steaf23.bingoreloaded.data.core.node.datatype.NodeDataType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FileConfigurationAccessor implements DataAccessor<BranchNode>
{
    private final JavaPlugin plugin;
    private final String location;

    private FileConfigurationAccessor(String filepath, JavaPlugin plugin) {
        this.location = filepath;
        this.plugin = plugin;
    }

    @Override
    public String getLocation() {
        return "";
    }

    @Override
    public void load() {

    }

    @Override
    public void saveChanges() {

    }

    @Override
    public Set<String> getKeys() {
        return Set.of();
    }

    @Override
    public void setString(String path, @NotNull String value) {

    }

    @Override
    public String getString(String path) {
        return "";
    }

    @Override
    public String getString(String path, String def) {
        return "";
    }

    @Override
    public <T> void setList(String path, NodeDataType<T> type, List<T> values) {

    }

    @Override
    public <T extends NodeSerializer> void setList(String path, List<T> values) {

    }

    @Override
    public <T> List<T> getList(String path, NodeDataType<T> dataType) {
        return List.of();
    }

    @Override
    public <T extends NodeSerializer> List<T> getList(String path, Class<T> classType) {
        return List.of();
    }

    @Override
    public void setSerializable(String path, NodeSerializer value) {

    }

    @Override
    public <T extends NodeSerializer> T getSerializable(String path, Class<T> classType) {
        return null;
    }

    @Override
    public <T extends NodeSerializer> T getSerializable(String path, Class<T> classType, T def) {
        return null;
    }

    @Override
    public void setBoolean(String path, boolean value) {

    }

    @Override
    public boolean getBoolean(String path) {
        return false;
    }

    @Override
    public boolean getBoolean(String path, Boolean def) {
        return false;
    }

    @Override
    public void setInt(String path, int value) {

    }

    @Override
    public int getInt(String path) {
        return 0;
    }

    @Override
    public int getInt(String path, int def) {
        return 0;
    }

    @Override
    public void setDouble(String path, double value) {

    }

    @Override
    public double getDouble(String path) {
        return 0;
    }

    @Override
    public double getDouble(String path, double def) {
        return 0;
    }

    @Override
    public void setBytes(String path, byte[] value) {

    }

    @Override
    public byte[] getBytes(String path) {
        return new byte[0];
    }

    @Override
    public void setItemStack(String path, ItemStack value) {

    }

    @Override
    public @NotNull ItemStack getItemStack(String path) {
        return null;
    }

    @Override
    public void setUUID(String path, UUID value) {

    }

    @Override
    public @Nullable UUID getUUID(String path) {
        return null;
    }

    @Override
    public void setLocation(String path, Location value) {

    }

    @Override
    public @Nullable Location getLocation(String path) {
        return null;
    }

    @Override
    public @NotNull Location getLocation(String path, @NotNull Location def) {
        return null;
    }

    @Override
    public void setStorage(String path, BranchNode value) {

    }

    @Override
    public @Nullable BranchNode getStorage(String path) {
        return null;
    }

    @Override
    public void erase(String path) {

    }

    @Override
    public boolean contains(String path) {
        return false;
    }
}
