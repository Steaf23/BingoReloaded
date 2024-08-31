package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StringDataType implements NodeDataType<String>
{
    @Override
    public void serializeValue(OutputStream stream, String value) throws IOException {
        NodeDataType.BYTES.serializeValue(stream, value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String deserializeValue(InputStream stream) throws IOException {
        return new String(NodeDataType.BYTES.deserializeValue(stream), StandardCharsets.UTF_8);
    }
}
