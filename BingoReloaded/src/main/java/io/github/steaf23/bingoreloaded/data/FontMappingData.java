package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import org.bukkit.NamespacedKey;

public class FontMappingData
{
    private final YmlDataManager data = BingoReloaded.createYmlDataManager("data/font_characters.yml");

    public String mapCharacter(String key) {
        return data.getConfig().getString(key, "");
    }
}
