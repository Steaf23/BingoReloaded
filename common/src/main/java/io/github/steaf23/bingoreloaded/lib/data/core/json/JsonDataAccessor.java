package io.github.steaf23.bingoreloaded.lib.data.core.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.Tag;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagList;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagTree;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * The Json data accessor is nothing more than a TagDataStorage that gets converted upon write and read to/from disk
 */
public class JsonDataAccessor extends JsonDataStorage implements DataAccessor
{
    private final ServerSoftware server;
    private final String filepath;
    private final boolean readOnly;

    public JsonDataAccessor(ServerSoftware server, String filepath, boolean readOnly) {
        this.server = server;
        this.filepath = filepath;
        this.readOnly = readOnly;
    }

    @Override
    public String getLocation() {
        return filepath;
    }

    @Override
    public String getFileExtension() {
        return ".json";
    }

    @Override
    public void load() {
        if (isInternalReadOnly()) {
            InputStream stream = server.getResource(getLocation() + getFileExtension());
            if (stream != null) {
                readJsonFromFile(this, stream);
            }
            return;
        }

        File file = new File(server.getDataFolder(), getLocation() + getFileExtension());
        if (!file.exists()) {
            server.saveResource(getLocation() + getFileExtension(), false);
        }

        try (InputStream input = new FileInputStream(file)) {
            readJsonFromFile(this, input);
        }
        catch (IOException e) {
            ConsoleMessenger.bug("Could not open json file for reading", this);
        }
    }

    @Override
    public void saveChanges() {
        if (isInternalReadOnly()) {
            return;
        }

        //FIXME: implement
        ConsoleMessenger.error("Writing json files from JsonDataAccessor is not implemented yet!");
    }

    @Override
    public boolean isInternalReadOnly() {
        return readOnly;
    }

    /**
     * Used by the JsonDataAccessor to convert json to DataStorage, but can be used standalone as well.
     * @param storage json data storage to store the parsed data into.
     * @param input input stream of a json file
     */
    public static void readJsonFromFile(JsonDataStorage storage, InputStream input) {
        storage.clear();

        JsonElement element = JsonParser.parseReader(new InputStreamReader(input));
        Tag<?> nbt = toTag(element);
        if (nbt.getType() != TagDataType.COMPOUND) {
            return;
        }
        storage.setTree((TagTree) nbt.getValue());
    }

    /**
     * A few notes on this conversion:
     *  - Numbers will be either parsed into a double for fractional values, or an int if small enough, else it will be parsed into a long
     *  - Booleans will be parsed into a boolean tag adapter. This means that the actual data stored is either 1b or 0b for true and false respectively.
     * @param jsonElement Element to convert to an NBT tag.
     * @return Converted json element as a tag.
     */
    private static Tag<?> toTag(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

            if (jsonPrimitive.isBoolean()) {
                return TagDataType.BOOLEAN.toTag(jsonPrimitive.getAsBoolean());

            } else if (jsonPrimitive.isNumber()) {
                Number number = jsonPrimitive.getAsNumber();

                long longVal = number.longValue();
                int intVal = number.intValue();
                double doubleVal = number.doubleValue();

                // the value has a fractional component, so we need to use a double tag.
                if (Math.abs(longVal - doubleVal) > 0.000001) {
                    return new Tag.DoubleTag(doubleVal);
                }
                // integer value probably got truncated, so it's better to use a long tag instead.
                if (intVal != longVal) {
                    return new Tag.LongTag(longVal);
                }
                else {
                    return new Tag.IntegerTag(intVal);
                }

            } else if (jsonPrimitive.isString()) {
                return new Tag.StringTag(jsonPrimitive.getAsString());
            }
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            TagList data = new TagList();

            try {
                for (JsonElement element : jsonArray) {
                    data.addTag(toTag(element));
                }
            } catch (IllegalArgumentException ex) {
                ConsoleMessenger.bug("Cannot convert json to NBT using different types within the same list.", JsonDataAccessor.class);
            }

            return new Tag.ListTag(data);
        } else if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            TagTree nbtCompound = new TagTree();

            for (Map.Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
                nbtCompound.putChild(jsonEntry.getKey(), toTag(jsonEntry.getValue()));
            }

            return new Tag.CompoundTag(nbtCompound);
        }

        // Best effort conversion to empty compound tag when the value is null or invalid
        ConsoleMessenger.bug("Cannot convert json to NBT, invalid data found.", JsonDataAccessor.class);
        return new Tag.CompoundTag(new TagTree());
    }


}
