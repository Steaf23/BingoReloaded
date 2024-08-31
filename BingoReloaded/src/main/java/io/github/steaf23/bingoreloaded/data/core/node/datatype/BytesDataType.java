package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import io.github.steaf23.playerdisplay.util.ConsoleMessenger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BytesDataType implements NodeDataType<byte[]>
{
    @Override
    public void serializeValue(OutputStream stream, byte[] value) throws IOException {
        NodeDataType.INT.serializeValue(stream, value.length);
        stream.write(value);
    }

    @Override
    public byte[] deserializeValue(InputStream stream) throws IOException {
        int length = NodeDataType.INT.deserializeValue(stream);
        try {
            return stream.readNBytes(length);
        } catch (IOException e)
        {
            ConsoleMessenger.bug(e.getMessage(), this);
        }
        return new byte[]{};
    }
}
