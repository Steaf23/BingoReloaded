package io.github.steaf23.bingoreloaded.data.core.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.node.NodeLikeData;
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

public class JsonDataStorage implements DataStorage {

    static class ElementHolder implements NodeLikeData.Node {
        private final JsonElement element;

        ElementHolder(JsonElement element) {
            this.element = element;
        }

        public JsonElement element() {
            return this.element;
        }
    }

    private final JsonObject jsonRoot;

    private JsonDataStorage() {
        this.jsonRoot = new JsonObject();
    }

    @Override
    public DataStorage createNew() {
        return new JsonDataStorage();
    }

    @Override
    public Set<String> getKeys() {
        return jsonRoot.keySet();
    }

    @Override
    public void setByte(String path, byte value) {
        JsonElement el = new JsonPrimitive(value);
        setElement(path, el);
    }

    @Override
    public byte getByte(String path, byte def) {
        return 0;
    }

    @Override
    public void setShort(String path, short value) {

    }

    @Override
    public short getShort(String path, short def) {
        return 0;
    }

    @Override
    public void setInt(String path, int value) {

    }

    @Override
    public int getInt(String path, int def) {
        return 0;
    }

    @Override
    public void setLong(String path, long value) {

    }

    @Override
    public long getLong(String path, long def) {
        return 0;
    }

    @Override
    public void setString(String path, @NotNull String value) {

    }

    @Override
    public @NotNull String getString(String path, String def) {
        return "";
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
    public void setBoolean(String path, boolean value) {

    }

    @Override
    public boolean getBoolean(String path) {
        return false;
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return false;
    }

    @Override
    public void setFloat(String path, float value) {

    }

    @Override
    public float getFloat(String path, float def) {
        return 0;
    }

    @Override
    public void setDouble(String path, double value) {

    }

    @Override
    public double getDouble(String path, double def) {
        return 0;
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
    public void setLocation(String path, @NotNull Location value) {

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
    public void setNamespacedKey(String path, @NotNull NamespacedKey value) {

    }

    @Override
    public @NotNull NamespacedKey getNamespacedKey(String path) {
        return null;
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

    private void setElement(String path, JsonElement element) {
        NodeLikeData.setNested(jsonRoot, path, element, () -> { return JsonObject(); });
    }
}
