package io.github.steaf23.bingoreloaded.lib.util;

import io.github.steaf23.bingoreloaded.lib.PlayerDisplay;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

public class PDCHelper
{
    public static Key createKey(String key)
    {
        return Key.key(PlayerDisplay.getPlugin(), key);
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
