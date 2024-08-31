package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UUIDDataType implements NodeDataType<java.util.UUID>
{
    @Override
    public void serializeValue(OutputStream stream, java.util.UUID value) throws IOException {
        NodeDataType.LONG.serializeValue(stream, value.getMostSignificantBits());
        NodeDataType.LONG.serializeValue(stream, value.getLeastSignificantBits());
    }

    @Override
    public java.util.UUID deserializeValue(InputStream stream) throws IOException {
        long mostSigBits = NodeDataType.LONG.deserializeValue(stream);
        long leastSigBits = NodeDataType.LONG.deserializeValue(stream);
        return new java.util.UUID(mostSigBits, leastSigBits);
    }
}
