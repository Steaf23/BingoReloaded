package io.github.steaf23.bingoreloaded.core.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.item.ItemText;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationData
{
    private final YmlDataManager language;
    private final YmlDataManager fallbackLanguage;

    private static final Pattern HEX_PATTERN = Pattern.compile("\\{#[a-fA-F0-9]{6}\\}");

    public TranslationData(BingoReloaded plugin)
    {
        this.language = new YmlDataManager(plugin, plugin.get().config().language);
        this.fallbackLanguage = new YmlDataManager(BingoReloaded.get(), "languages/en_us.yml");
    }

    public String translate(String key, String... args)
    {
        String rawTranslation = get(key);
        rawTranslation = convertColors(rawTranslation);

        for (int i = 0; i < args.length; i++)
        {
            rawTranslation = rawTranslation.replace("{" + i + "}", args[i]);
        }
        return rawTranslation;
    }

    /**
     * convert translated string with arguments to ItemText and preserve argument order, like translate() does
     * @param key
     * @param args
     * @return An array of itemText where each element is a line,
     *  where each line is defined by '\n' in the translated string.
     */
    public ItemText[] translateToItemText(String key, Set<ChatColor> modifiers, ItemText... args)
    {
        //TODO: fix issue where raw translations cannot convert the colors defined in lang files properly on items
        String rawTranslation = get(key);
        rawTranslation = convertColors(rawTranslation);
        TextComponent.fromLegacyText(rawTranslation);

        List<ItemText> result = new ArrayList<>();
        String[] lines = rawTranslation.split("\\n");
        String[] pieces;
        for (int i = 0; i < lines.length; i++)
        {
            ItemText line = new ItemText(modifiers.toArray(new ChatColor[]{}));
            pieces = lines[i].split("\\{");
            for (String piece : pieces)
            {
                String pieceToAdd = piece;
                for (int argIdx = 0; argIdx < args.length; argIdx++)
                {
                    if (pieceToAdd.contains(argIdx + "}"))
                    {
                        line.add(args[argIdx]);
                        pieceToAdd = pieceToAdd.replace(i + "}", "");
                        break;
                    }
                }
                line.addText(pieceToAdd);
            }
            result.add(line);
        }
        return result.toArray(new ItemText[]{});
    }

    public String itemName(String key)
    {
        return translate(key + ".name");
    }

    public String[] itemDescription(String key)
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

    private String get(String path)
    {
        String def = "" + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + "`" + path + "`";
        // avoid weird MemorySection String prints instead of translation failed message.
        if (language.getConfig().getConfigurationSection(path) == null)
            return language.getConfig().getString(path, def);
        else if (fallbackLanguage.getConfig().getConfigurationSection(path) == null)
            return fallbackLanguage.getConfig().getString(path, def);
        return def;
    }
}
