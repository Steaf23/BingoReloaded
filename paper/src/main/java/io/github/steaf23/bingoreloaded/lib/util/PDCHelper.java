package io.github.steaf23.bingoreloaded.lib.util;

import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Nullable;

public class PDCHelper
{
    public static Key createKey(@Subst("key") String key)
    {
        @Subst("name") String name = PlatformResolver.get().getExtensionInfo().name();
        return Key.key(name, key);
    }

    public static PersistentDataContainer addStringToPdc(PersistentDataContainer pdc, String key, @Nullable String value) {
        if (key == null) {
            return pdc;
        }

        Key advKey = PDCHelper.createKey("item." + key);
        NamespacedKey namespacedKey = new NamespacedKey(advKey.namespace(), advKey.value());
        if (value == null)
            pdc.remove(namespacedKey);
        else
            pdc.set(namespacedKey, PersistentDataType.STRING, value);
        return pdc;
    }

    public static String getStringFromPdc(PersistentDataContainer pdc, String key) {
        Key advKey = PDCHelper.createKey("item." + key);
        NamespacedKey namespacedKey = new NamespacedKey(advKey.namespace(), advKey.value());
        return pdc.getOrDefault(namespacedKey, PersistentDataType.STRING, "");
    }
}
