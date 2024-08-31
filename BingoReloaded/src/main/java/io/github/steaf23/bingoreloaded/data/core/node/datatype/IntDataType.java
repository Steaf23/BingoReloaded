package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IntDataType implements NodeDataType<Integer>
{
    @Override
    public void serializeValue(OutputStream stream, Integer value) throws IOException {
        stream.write((value >> 24) & 0xFF);
        stream.write((value >> 16) & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write(value & 0xFF);
    }

    @Override
    public Integer deserializeValue(InputStream stream) throws IOException {
        return (stream.read() << 24) |
                (stream.read() << 16) |
                (stream.read() << 8) |
                stream.read();
    }
}
