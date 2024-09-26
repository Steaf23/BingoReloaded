package io.github.steaf23.bingoreloaded.data.core.tag;

import io.github.steaf23.playerdisplay.util.ConsoleMessenger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TagList
{
    // Use some default tag type (since when the list is empty, its type won't matter
    TagDataType<?> tagType = TagDataType.BYTE;

    private final List<Tag<?>> tags = new ArrayList<>();

    public void addTag(Tag<?> tag) throws IllegalArgumentException {
        if (tags.isEmpty()) {
            tagType = tag.getType();
        }

        if (!tagType.equals(tag.getType())) {
            throw new IllegalArgumentException("Cannot add tag with different type to a list of a specified type!");
        }

        tags.add(tag);
    }

    public void removeTag(int index) {
        tags.remove(index);
    }

    public Tag<?> getTag(int index) {
        return tags.get(index);
    }

    public int size() {
        return tags.size();
    }

    /*
        A byte denoting the tag ID of the list's contents,
        followed by the list's length as a signed integer (4 bytes),
        then length number of payloads that correspond to the given tag ID.
    */
    public void getPayload(ByteArrayOutputStream stream) {
        stream.write(tagType.getId());
        stream.writeBytes(ByteHelper.intToBytes(tags.size()));

        for (Tag<?> t : tags) {
            t.writePayloadBytes(stream);
        }
    }

    public static TagList fromPayload(ByteArrayInputStream stream) {
        TagDataType<?> type = TagDataType.getTypeFromId(stream.read());
        if (type == null) {
            ConsoleMessenger.bug("Could not read tag list from input stream", new TagList());
            return new TagList();
        }
        int size = ByteHelper.readInt(stream);

        TagList list = new TagList();
        for (int i = 0; i < size; i++) {
            //TODO: create function to turn some byte string into a typed tag
            list.addTag(type.createTagFromStream(stream)); // read buffer until next tag ends
        }
        return list;
    }

    public <T> List<T> getList(TagDataType<T> type) {
        if (type != tagType)
        {
            return List.of();
        }

        List<T> result = new ArrayList<>();
        for (Tag<?> t : tags)
        {
            result.add((T) t.getValue());
        }
        return result;
    }

    public List<Tag<?>> getTags() {
        return tags;
    }
}
