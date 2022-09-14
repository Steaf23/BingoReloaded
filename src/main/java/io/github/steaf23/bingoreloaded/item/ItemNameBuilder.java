package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

//A builder used for naming items through NBT data.
// This means it can also be used to translate text from the minecraft key namespace to the client's language.
public class ItemNameBuilder
{
    public static String prefix = "{display:{Name:\"[";
    public static String suffix = "]\"}}";
    public List<String> args = new ArrayList<>();

    public ItemNameBuilder(ChatColor color, boolean italic, boolean bold,
                           boolean strikethrough, boolean underlined, boolean obfuscated)
    {
        args.add("{\\\"text\\\":\\\"\\\"" +
                ",\\\"color\\\":\\\"#" + Integer.toHexString(color.getColor().getRGB()).substring(2) + "\\\"" +
                ",\\\"italic\\\":\\\"" + italic + "\\\"" +
                ",\\\"bold\\\":\\\"" + bold + "\\\"" +
                ",\\\"strikethrough\\\":\\\"" + strikethrough + "\\\"" +
                ",\\\"underlined\\\":\\\"" + underlined + "\\\"" +
                ",\\\"obfuscated\\\":\\\"" + obfuscated + "\\\"" +
                "}");
    }

    public ItemNameBuilder text(String text)
    {
        args.add(createText(text));
        return this;
    }

    public ItemNameBuilder translate(String itemKey)
    {
        args.add(createTranslate(itemKey));
        return this;
    }

    public ItemStack build(ItemStack stack)
    {
        String finalName = prefix;
        for (int i = 0; i < args.size(); i++)
        {
            if (i > 0)
            {
                finalName += ",";
            }
            finalName += args.get(i);
        }
        finalName += suffix;
        return Bukkit.getUnsafe().modifyItemStack(stack, finalName);
    }

    public static String getTranslateKey(Material item)
    {
        String key = item.isBlock() ? "block" : "item";
        key += ".minecraft." + item.getKey().getKey();
        return key;
    }

    public static String createText(String text)
    {
        return "{\\\"text\\\":\\\"" + text + "\\\"}";
    }

    public static String createTranslate(String key)
    {
        return "{\\\"translate\\\":\\\"" + key + "\\\"}";
    }
}
