package io.github.steaf23.bingoreloaded.lib.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class PDCHelper
{
    public static PersistentDataContainer addStringToPdc(PersistentDataContainer pdc, String key, @Nullable String value) {
        if (key == null) {
            return pdc;
        }

        Key advKey = BingoReloaded.resourceKey("item." + key);
        NamespacedKey namespacedKey = new NamespacedKey(advKey.namespace(), advKey.value());
        if (value == null)
            pdc.remove(namespacedKey);
        else
            pdc.set(namespacedKey, PersistentDataType.STRING, value);
        return pdc;
    }

    public static String getStringFromPdc(PersistentDataContainer pdc, String key) {
        Key advKey = BingoReloaded.resourceKey("item." + key);
        NamespacedKey namespacedKey = new NamespacedKey(advKey.namespace(), advKey.value());
        return pdc.getOrDefault(namespacedKey, PersistentDataType.STRING, "");
    }
}
