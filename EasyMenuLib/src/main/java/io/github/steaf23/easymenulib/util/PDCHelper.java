package io.github.steaf23.easymenulib.util;

import io.github.steaf23.easymenulib.EasyMenuLibrary;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class PDCHelper
{
    public static NamespacedKey createKey(String key)
    {
        return new NamespacedKey(EasyMenuLibrary.getPlugin(), key);
    }

    public static PersistentDataContainer addStringToPdc(PersistentDataContainer pdc, String key, @Nullable String value) {
        if (key == null) {
            return pdc;
        }

        if (value == null)
            pdc.remove(PDCHelper.createKey("item." + key));
        else
            pdc.set(PDCHelper.createKey("item." + key), PersistentDataType.STRING, value);
        return pdc;
    }

    public static String getStringFromPdc(PersistentDataContainer pdc, String key) {
        return pdc.getOrDefault(PDCHelper.createKey("item." + key), PersistentDataType.STRING, "");
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
