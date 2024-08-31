package io.github.steaf23.bingoreloaded.data.core.node;

import io.github.steaf23.bingoreloaded.data.core.node.datatype.NodeDataType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataNode<T> implements Node {

    private final NodeDataType<T> type;
    private T value;

    public DataNode(NodeDataType<T> type, @NotNull T value) {
        this.type = type;
        this.value = value;
    }

    public DataNode(NodeDataType<T> type, InputStream stream) throws IOException {
        this.type = type;
        deserialize(stream);
    }

    @Override
    public void serialize(OutputStream stream) throws IOException {
        type.serializeValue(stream, value);
    }

    @Override
    public void deserialize(InputStream stream) throws IOException {
        value = type.deserializeValue(stream);
    }

    public T getValue() {
        return value;
    }

    public NodeDataType<T> getType() {
        return type;
    }
}




