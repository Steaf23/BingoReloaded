package io.github.steaf23.bingoreloaded.data.core;

import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.data.core.tag.adapters.LocationStorageSerializer;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DataStorageSerializerRegistry
{
    private static final Map<Class<?>, DataStorageSerializer<?>> serializers = new HashMap<>();

    static {
        addSerializer(new LocationStorageSerializer(), Location.class);
    }

    public static <T> void addSerializer(DataStorageSerializer<T> serializer, Class<T> classType) {
        serializers.put(classType, serializer);
    }

    public static <T> @Nullable DataStorageSerializer<T> getSerializer(Class<T> classType) {
        if (!serializers.containsKey(classType)) {
            ConsoleMessenger.error("No data storage serializer registered for given class type " + classType.getName());
        }
        return (DataStorageSerializer<T>) serializers.get(classType);
    }
}
