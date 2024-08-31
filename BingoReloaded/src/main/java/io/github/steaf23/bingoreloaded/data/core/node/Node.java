package io.github.steaf23.bingoreloaded.data.core.node;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public interface Node
{
    default Set<String> getKeys() {
        return Set.of();
    };

    void serialize(OutputStream stream) throws IOException;
    void deserialize(InputStream stream) throws IOException;
}
