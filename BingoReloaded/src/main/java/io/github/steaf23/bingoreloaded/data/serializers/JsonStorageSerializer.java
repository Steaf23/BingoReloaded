package io.github.steaf23.bingoreloaded.data.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JsonStorageSerializer implements DataStorageSerializer<JsonObject>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull JsonObject value) {
        readIntoStorage(storage, value);
    }

    @Override
    public @Nullable JsonObject fromDataStorage(@NotNull DataStorage storage) {
        return null;
    }

    private static void insertElement(String location, DataStorage storage, JsonElement element) {
        if (element.isJsonArray()) {
            List<JsonElement> elements = element.getAsJsonArray().asList();
            if (elements.isEmpty()) {
                storage.setList(location, TagDataType.BYTE, List.of());
                return;
            }
            JsonElement first = element.getAsJsonArray().get(0);
            TagDataType<?> firstType = getTypeOfElement(first);

        }

        TagDataType<?> type = getTypeOfElement(element);
        if (type == TagDataType.BYTE) {

        }
        else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                storage.setString(location, primitive.getAsString());
            }
            else if (primitive.isBoolean()) {
                storage.setBoolean(location, primitive.getAsBoolean());
            }
            else if (primitive.isNumber()) {
                storage.setLong(location, primitive.getAsLong());
            }
        }
        else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            DataStorage nested = storage.createNew();
            readIntoStorage(nested, obj);

            storage.setStorage(location, nested);
        }
    }

    private static TagDataType<?> getTypeOfElement(JsonElement element) {
        if (element.isJsonArray()) {
            List<JsonElement> elements = element.getAsJsonArray().asList();
            if (elements.isEmpty()) {
                return TagDataType.LIST;
            }
            JsonElement first = element.getAsJsonArray().get(0);

        }
        else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return TagDataType.STRING;
            }
            else if (primitive.isBoolean()) {
                return TagDataType.BYTE;
            }
        }
        return TagDataType.BYTE;
    }

    private static void readIntoStorage(DataStorage storage, JsonObject object) {
        for (String name : object.keySet()) {
            insertElement(name, storage, object.get(name));
        }
    }

    private static <T> void readIntoList(List<T> storage, JsonObject object, TagDataType<T> type) {

    }
}
