package io.github.steaf23.bingoreloaded.data;

import org.bukkit.ChatColor;

public class TranslationData
{
    private static final YmlDataManager data = new YmlDataManager(ConfigData.getConfig().language);

    public static String get(String path)
    {
        String def = ChatColor.GRAY + "--no translation for '" + path + "' in " + ConfigData.getConfig().language + "--";
        return data.getConfig().getString(path, def);
    }
}
