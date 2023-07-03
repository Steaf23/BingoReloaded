package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PDCHelper
{

    public static NamespacedKey createKey(String key)
    {
        return new NamespacedKey(BingoReloaded.getInstance(), key);
    }

    public static PersistentDataContainer setBoolean(PersistentDataContainer container, String key, boolean value)
    {
        container.set(createKey(key), PersistentDataType.BYTE, (byte)(value ? 1 : 0));
        return container;
    }

    public static boolean getBoolean(PersistentDataContainer container, String key, boolean def)
    {
        return container.getOrDefault(createKey(key), PersistentDataType.BYTE, (byte)(def ? 1 : 0)) == 0 ? false : true;
    }

    public static boolean hasBoolean(PersistentDataContainer container, String key)
    {
        return container.has(createKey(key), PersistentDataType.BYTE);
    }
}
