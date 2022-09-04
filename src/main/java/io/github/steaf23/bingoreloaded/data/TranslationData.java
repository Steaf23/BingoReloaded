package io.github.steaf23.bingoreloaded.data;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import javax.swing.text.JTextComponent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationData
{
    private static final YmlDataManager data = new YmlDataManager(ConfigData.getConfig().language);

    private static final Pattern HEX_PATTERN = Pattern.compile("\\{#[a-fA-F0-9]{6}\\}");

    public static String get(String path)
    {
        String def = ChatColor.GRAY + "--no translation for '" + path + "' in " + ConfigData.getConfig().language + "--";
        return data.getConfig().getString(path, def);
    }

    public static String translate(String key)
    {
        String rawTranslation = get(key);
        return convertColors(rawTranslation);
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
}
