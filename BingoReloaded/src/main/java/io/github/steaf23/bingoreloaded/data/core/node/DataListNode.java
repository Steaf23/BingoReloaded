package io.github.steaf23.bingoreloaded.data.core.node;

import io.github.steaf23.bingoreloaded.data.core.node.datatype.NodeDataType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataListNode<T> implements Node {

    private final NodeDataType<T> type;
    private final List<T> values = new ArrayList<>();

    public DataListNode(NodeDataType<T> type, @NotNull List<T> values) {
        this.type = type;
        this.values.addAll(values);
    }

    public DataListNode(NodeDataType<T> type, InputStream stream) throws IOException {
        this.type = type;
        deserialize(stream);
    }

    @Override
    public void serialize(OutputStream stream) throws IOException {
        NodeDataType.INT.serializeValue(stream, values.size());
        for (T v : values) {
            type.serializeValue(stream, v);
        }
    }

    @Override
    public void deserialize(InputStream stream) throws IOException {
        values.clear();
        int size = NodeDataType.INT.deserializeValue(stream);
        for (int i = 0; i < size; i++) {
            values.add(type.deserializeValue(stream));
        }
    }

    public List<T> getValues() {
        return values;
    }

    public NodeDataType<T> getType() {
        return type;
    }
}