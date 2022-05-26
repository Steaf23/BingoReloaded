package me.steven.bingoreloaded.data;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class TranslationData
{
    private static final Map<String, YMLDataManager> translations = new HashMap<>();
    private static String language = "english_us";

    public static void populateTranslations()
    {
        YMLDataManager languages = new YMLDataManager("translations.yml");
        for (Map<?, ?> map : languages.getConfig().getMapList("languages"))
        {
            for (Object key : map.keySet())
            {
                if (key instanceof String lang)
                {
                    MessageSender.log(lang);
                    translations.put(lang, new YMLDataManager(languages.getConfig().getString("languages." + lang)));
                    break;
                }
            }

        }
    }

    public void selectLanguage(String language)
    {
        TranslationData.language = language;
    }

    public static String get(String path)
    {
        String def = ChatColor.GRAY + "--no translation for '" + path + "' in " + language + "--";
        if (translations.containsKey(language))
        {
            return translations.get(language).getConfig().getString(path, def);
        }
        return def;
    }
}
