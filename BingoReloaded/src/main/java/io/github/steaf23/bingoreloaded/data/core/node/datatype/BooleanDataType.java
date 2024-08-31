package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BooleanDataType implements NodeDataType<Boolean>
{
    @Override
    public void serializeValue(OutputStream stream, Boolean value) throws IOException {
        stream.write(value ? 1 : 0);
    }

    @Override
    public Boolean deserializeValue(InputStream stream) throws IOException {
        return stream.read() == 1;
    }
}
