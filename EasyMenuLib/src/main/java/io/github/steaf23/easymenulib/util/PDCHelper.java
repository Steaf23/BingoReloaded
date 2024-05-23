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
}
