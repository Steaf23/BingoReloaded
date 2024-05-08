package io.github.steaf23.easymenulib.util;

import io.github.steaf23.easymenulib.EasyMenuLibrary;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PDCHelper
{
    public static NamespacedKey createKey(String key)
    {
        return new NamespacedKey(EasyMenuLibrary.getPlugin(), key);
    }
}
