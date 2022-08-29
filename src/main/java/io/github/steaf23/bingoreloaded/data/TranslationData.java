package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.MessageSender;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class TranslationData
{
    private static final Map<String, YmlDataManager> translations = new HashMap<>();
    private static String language = "english_us";

    public static void populateTranslations()
    {
        YmlDataManager languages = new YmlDataManager("translations.yml");
        for (Map<?, ?> map : languages.getConfig().getMapList("languages"))
        {
            for (Object key : map.keySet())
            {
                if (key instanceof String lang)
                {
                    MessageSender.log(lang);
                    translations.put(lang, new YmlDataManager(languages.getConfig().getString("languages." + lang)));
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
