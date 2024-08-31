package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class DoubleDataType implements NodeDataType<Double>
{
    @Override
    public void serializeValue(OutputStream stream, Double value) throws IOException {
        stream.write(ByteBuffer.allocate(8).putDouble(value).array());
    }

    @Override
    public Double deserializeValue(InputStream stream) throws IOException {
        return ByteBuffer.wrap(stream.readNBytes(8)).getDouble();
    }
}
