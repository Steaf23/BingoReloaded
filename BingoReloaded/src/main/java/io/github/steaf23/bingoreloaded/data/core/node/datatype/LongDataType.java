package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LongDataType implements NodeDataType<Long>
{
    @Override
    public void serializeValue(OutputStream stream, Long value) throws IOException {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(value & 0xFF);
            value >>= 8;
        }
        stream.write(result);
    }

    @Override
    public Long deserializeValue(InputStream stream) throws IOException {
        byte[] bytes = stream.readNBytes(8);
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }
}
