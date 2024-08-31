package io.github.steaf23.bingoreloaded.data.core.node;

import io.github.steaf23.bingoreloaded.data.core.node.datatype.NodeDataType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class NodeBuilder
{
    private final BranchNode node = new BranchNode();

    public NodeBuilder withString(String path, String value) {
        node.setString(path, value);
        return this;
    }

    public <T> NodeBuilder withList(String path, NodeDataType<T> type, List<T> values) {
        node.setList(path, type, values);
        return this;
    }

    public NodeBuilder withInt(String path, int value) {
        node.setInt(path, value);
        return this;
    }

    public NodeBuilder withBoolean(String path, boolean value) {
        node.setBoolean(path, value);
        return this;
    }

    public NodeBuilder withDouble(String path, double value) {
        node.setDouble(path, value);
        return this;
    }

    public NodeBuilder withBytes(String path, byte[] value) {
        node.setBytes(path, value);
        return this;
    }

    public NodeBuilder withItemStack(String path, ItemStack value) {
        node.setItemStack(path, value);
        return this;
    }

    public NodeBuilder withSerializable(String path, NodeSerializer value) {
        node.setSerializable(path, value);
        return this;
    }

    public NodeBuilder withUUID(String path, UUID value) {
        node.setUUID(path, value);
        return this;
    }

    public NodeBuilder withLocation(String path, Location value) {
        node.setLocation(path, value);
        return this;
    }

    public BranchNode getNode() {
        return node;
    }

    public static List<String> enumSetToList(EnumSet<? extends Enum<?>> set)
    {
        List<String> list = new ArrayList<>();
        set.forEach(entry -> list.add(entry.name()));
        return list;
    }

    public static <E extends Enum<E>> EnumSet<E> enumSetFromList(Class<E> enumType, List<String> list)
    {
        EnumSet<E> result = EnumSet.noneOf(enumType);
        list.forEach(entry -> result.add(Enum.<E>valueOf(enumType, entry)));
        return result;
    }
}
