package io.github.steaf23.bingoreloaded.lib.data.core.tag;

import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TagTree
{
    private final Map<String, Tag<?>> tags = new HashMap<>();

    public Set<String> getKeys() {
        return tags.keySet();
    }

    public Tag<?> getChild(String path) {
        return tags.get(path);
    }

    public void putChild(String path, Tag<?> data) {
        if (data == null)
        {
            tags.remove(path);
            return;
        }
        tags.put(path, data);
    }

    public void removeChild(String path) {
        tags.remove(path);
    }

    public boolean containsChild(String path) {
        return tags.containsKey(path);
    }

    public boolean isEmpty() {
        return tags.isEmpty();
    }

    public void getPayload(ByteArrayOutputStream stream) {
        for (String name : getKeys()) {
            Tag<?> tag = tags.get(name);
            stream.write(tag.getType().getId());
            ByteHelper.writeString(name, stream);
            tag.writePayloadBytes(stream);
        }
        stream.write(0);
    }

    public static TagTree fromPayload(ByteArrayInputStream stream) {
        TagTree tree = new TagTree();

        byte id = (byte) stream.read();
        while (id != 0) {
            String name = ByteHelper.readString(stream);
            TagDataType<?> type = TagDataType.getTypeFromId(id);
            if (type == null) {
                ConsoleMessenger.error("Error while decoding NBT input stream");
                return null;
            }
            tree.putChild(name, type.createTagFromStream(stream));
            id = (byte) stream.read();
        }

        return tree;
    }
}
