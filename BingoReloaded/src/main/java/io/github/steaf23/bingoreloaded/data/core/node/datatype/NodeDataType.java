package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import io.github.steaf23.bingoreloaded.data.core.node.NodeSerializer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public interface NodeDataType<T>
{
    NodeDataType<byte[]> BYTES = new BytesDataType();
    NodeDataType<String> STRING = new StringDataType();
    NodeDataType<Integer> INT = new IntDataType();
    NodeDataType<Long> LONG = new LongDataType();
    NodeDataType<Boolean> BOOLEAN = new BooleanDataType();
    NodeDataType<Double> DOUBLE = new DoubleDataType();
    NodeDataType<ItemStack> ITEM_STACK = new ItemStackDataType();
    NodeDataType<Location> LOCATION = new LocationDataType();
    NodeDataType<UUID> UUID = new UUIDDataType();
    NodeDataType<NodeSerializer> SERIALIZABLE = new SerializableDataType<>();

    void serializeValue(OutputStream stream, T value) throws IOException;
    T deserializeValue(InputStream stream) throws IOException;

    List<NodeDataType<?>> types = List.of(
            BYTES,
            STRING,
            INT,
            BOOLEAN,
            DOUBLE,
            ITEM_STACK,
            LOCATION,
            UUID,
            SERIALIZABLE
    );

    static @Nullable NodeDataType<?> typeFromIndex(int id) {
        return types.get(id);
    }

    static @Nullable Integer indexFromType(NodeDataType<?> type) {
        int index = types.indexOf(type);
        return index == -1 ? null : index;
    }
}
