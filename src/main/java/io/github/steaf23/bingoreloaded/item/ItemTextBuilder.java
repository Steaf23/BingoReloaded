package io.github.steaf23.bingoreloaded.item;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//A builder used for naming items through NBT data.
// This means it can also be used to translate text from the minecraft key namespace to the client's language.
public class ItemTextBuilder
{
    public static String namePrefix = "{display:{Name:\'[";
    public static String lorePrefix = "{display:{Lore:\'[";
    public static String suffix = "]\'}}";
    public List<String> args = new ArrayList<>();

    public ItemTextBuilder(ChatColor color, String... modifiers)
    {
        args.add("{\"text\":\"\"" +
                ",\"color\":\"#" + Integer.toHexString(color.getColor().getRGB()).substring(2) + "\"" +
                ",\"italic\":\"" + Arrays.stream(modifiers).anyMatch((m) -> m == "italic") + "\"" +
                ",\"bold\":\"" + Arrays.stream(modifiers).anyMatch((m) -> m == "bold") + "\"" +
                ",\"strikethrough\":\"" + Arrays.stream(modifiers).anyMatch((m) -> m == "strikethrough") + "\"" +
                ",\"underlined\":\"" + Arrays.stream(modifiers).anyMatch((m) -> m == "underlined") + "\"" +
                ",\"obfuscated\":\"" + Arrays.stream(modifiers).anyMatch((m) -> m == "obfuscated") + "\"" +
                "}");
    }

    public ItemTextBuilder text(String text)
    {
        args.add(createText(text));
        return this;
    }

    public ItemTextBuilder translate(String itemKey)
    {
        args.add(createTranslate(itemKey));
        return this;
    }

    public ItemStack buildName(ItemStack stack)
    {
        String finalName = namePrefix;
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

    public ItemStack buildDescription(ItemStack stack)
    {
        String finalName = lorePrefix;
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

    public static String getItemKey(Material item)
    {
        String key = item.isBlock() ? "block" : "item";
        key += ".minecraft." + item.getKey().getKey();
        return key;
    }

    public static String getAdvancementTitleKey(Advancement advancement)
    {

        return getAdvancement(advancement) + ".title";
    }

    public static String getAdvancementDescKey(Advancement advancement)
    {
        return getAdvancement(advancement) + ".description";
    }

    private static String getAdvancement(Advancement advancement)
    {
        String result = advancement.getKey().getKey();
        result = result.replace("/", ".");
        switch (result) // Needed to correct Spigot on some advancement names vs how they appear in the lang files
        {
            case "husbandry.obtain_netherite_hoe":
                result = "husbandry.netherite_hoe";
                break;
            case "husbandry.bred_all_animals":
                result = "husbandry.breed_all_animals";
                break;
        }
        return "advancements." + result;
    }

    public static String createText(String text)
    {
        return "{\"text\":\"" + text + "\"}";
    }

    public static String createTranslate(String key)
    {
        return "{\"translate\":\"" + key + "\"}";
    }
}
