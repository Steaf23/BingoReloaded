package io.github.steaf23.bingoreloaded.data;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import javax.swing.text.JTextComponent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationData
{
    private static final YmlDataManager data = new YmlDataManager(ConfigData.instance.language);

    private static final Pattern HEX_PATTERN = Pattern.compile("\\{#[a-fA-F0-9]{6}\\}");

    public static String translate(String key, String... args)
    {
        String rawTranslation = get(key);
        rawTranslation = convertColors(rawTranslation);

        for (int i = 0; i < args.length; i++)
        {
            rawTranslation = rawTranslation.replace("{" + i + "}", args[i]);
        }
        return rawTranslation;
    }

    public static String itemName(String key)
    {
        return translate(key + ".name");
    }

    public static String[] itemDescription(String key)
    {
        return translate(key + ".desc").split("\\n");
    }

    /**
     * @param input The input string, can look something like this: "{#00bb33}Hello, I like to &2&lDance && &rSing!"
     * @return Legacy text string that can be used in TextComponent.fromLegacyText()
     */
    public static String convertColors(String input)
    {
        String part = input;
        part = part.replaceAll("(?<!&)&(?!&)", "§");
        part = part.replaceAll("&&", "&");

        Matcher matcher = HEX_PATTERN.matcher(part);
        while (matcher.find())
        {
            String match = matcher.group();
            String color = match.replaceAll("[\\{\\}]", "");
            part = part.replace(match, "" + net.md_5.bungee.api.ChatColor.of(color));
        }

        return part;
    }

    private static String get(String path)
    {
        String def = ChatColor.GRAY + "-- No translation for '" + path + "' in " + ConfigData.instance.language + " --";
        // avoid weird MemorySection String prints instead of translation failed message.
        if (data.getConfig().getConfigurationSection(path) == null)
            return data.getConfig().getString(path, def);
        return def;
    }
}
