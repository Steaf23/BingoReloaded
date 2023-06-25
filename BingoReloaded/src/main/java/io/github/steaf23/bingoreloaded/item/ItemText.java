package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.util.SmallCaps;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A builder used for naming items through NBT data.
 * This means it can also be used to translate text from the minecraft key namespace to the client's language.
 * Can also return bungee's ChatComponents using asComponent.
 */
public class ItemText
{
    private final String type;
    private final String text;
    private String modifiers;
    private final List<ItemText> children;
    private final static YmlDataManager statTranslation = BingoReloaded.createYmlDataManager("data/stat_translation.yml");

    public ItemText(ChatColor... modifiers)
    {
        this("", modifiers);
    }

    public ItemText(String text, ChatColor... modifiers)
    {
        this("text", text, createModifiers(modifiers));
    }

    private ItemText(String type, String text, String modifiers, ItemText... children)
    {
        this.type = type;
        this.text = text;
        this.modifiers = modifiers;
        this.children = new ArrayList<>();
        this.children.addAll(Arrays.asList(children));
    }

    public BaseComponent asComponent()
    {
        BaseComponent root = new TextComponent();
        root.setExtra(Arrays.stream(ComponentSerializer.parse(asJsonRoot())).collect(Collectors.toList()));
        return root;
    }

    public String asLegacyString()
    {
        return asComponent().toLegacyText();
    }

    public String asJsonRoot()
    {
        // Make the root not italic, to circumvent the default behavior.
        if (!modifiers.contains(",\"italic\""))
        {
            modifiers += ",\"italic\":false";
        }
        return asJsonString();
    }

    public String asJsonString()
    {
        StringBuilder result = new StringBuilder("{\"" + type + "\":\"" + text + "\"" + modifiers);

        if (children.size() == 0)
            return result + "}";

        result.append(",\"extra\":[");
        for (int i = 0; i < children.size(); i ++)
        {
            if (i != 0) result.append(",");
            result.append(children.get(i).asJsonString());
        }

        result.append("]}");
        return result.toString();
    }

    public static ItemText combine(ItemText... input)
    {
        ItemText result = new ItemText();
        for (ItemText part : input)
        {
            result.add(part);
        }
        return result;
    }

    public ItemText add(ItemText other)
    {
        children.add(other);
        return other;
    }

    /**
     * Add a simple text element
     * @param text
     * @param modifiers
     * @return
     */
    public ItemText addText(String text, ChatColor... modifiers)
    {
        return add(new ItemText("text", text, ItemText.createModifiers(modifiers)));
    }

    public ItemText addSmallCapsText(String text, ChatColor... modifiers)
    {
        return add(new ItemText("text", SmallCaps.toSmallCaps(text), ItemText.createModifiers(modifiers)));
    }

    /**
     * Adds a translated piece of text based on minecraft's lang files using the provided translation key
     * @param key
     * @param modifiers
     * @return
     */
    public  ItemText addTranslation(String key, ChatColor... modifiers)
    {
        return add(translateComponent(key, new ItemText[]{}, modifiers));
    }

    public  ItemText addTranslation(String key, ItemText[] jsonArgs, ChatColor... modifiers)
    {
        return add(translateComponent(key, jsonArgs, modifiers));
    }

    public ItemText addItemName(Material item)
    {
        return addTranslation(itemKey(item));
    }

    public ItemText addAdvancementTitle(@NonNull Advancement advancement)
    {
        return addTranslation(advancementKey(advancement) + ".title");
    }

    public ItemText addAdvancementDescription(@NonNull Advancement advancement)
    {
        return addTranslation(advancementKey(advancement) + ".description");
    }

    public ItemText addStatistic(Statistic statistic, ItemText... with)
    {
        return addTranslation(statisticKey(statistic), with);
    }

    public ItemText addEntityName(EntityType entity)
    {
        return addTranslation(entityKey(entity));
    }

    public static ItemStack buildItemText(ItemStack itemStack, ItemText nameText, ItemText... loreText)
    {
        StringBuilder newText = new StringBuilder("{display:{Name:'[" + nameText.asJsonRoot() + "]'");

        if (loreText.length == 0 || (loreText.length == 1 && loreText[0].asJsonRoot().isEmpty()))
            return Bukkit.getUnsafe().modifyItemStack(itemStack, newText + "}}");

        newText.append(",Lore:[");
        for (int i = 0; i < loreText.length; i ++)
        {
            if (i != 0) newText.append(",");
            newText.append("'")
                    .append(loreText[i].asJsonString())
                    .append("'");
        }

        newText.append("]}}");

        return Bukkit.getUnsafe().modifyItemStack(itemStack, newText.toString());
    }

    public static String createModifiers(ChatColor... modifiers)
    {
        Set<ChatColor> modifierSet = new HashSet<>(Arrays.stream(modifiers).collect(Collectors.toList()));
        StringBuilder mods = new StringBuilder();

        for (ChatColor mod : modifierSet)
        {
            if (mod.getColor() != null)
            {
                mods.append(",\"color\":\"#")
                        .append(Integer.toHexString(mod.getColor().getRGB()).substring(2))
                        .append("\"");
                break;
            }
        }
        if (modifierSet.contains(ChatColor.ITALIC)) mods.append(",\"italic\":true");
        if (modifierSet.contains(ChatColor.BOLD)) mods.append(",\"bold\":true");
        if (modifierSet.contains(ChatColor.STRIKETHROUGH)) mods.append(",\"strikethrough\":true");
        if (modifierSet.contains(ChatColor.UNDERLINE)) mods.append(",\"underlined\":true");
        if (modifierSet.contains(ChatColor.MAGIC)) mods.append(",\"obfuscated\":true");
        return mods.toString();
    }

    /**
     * jsonArgs for simple text can be created using createText()
     * @param key
     * @param jsonArgs
     * @param modifiers
     * @return
     */
    private static ItemText translateComponent(String key, ItemText[] jsonArgs, ChatColor... modifiers)
    {
        if (jsonArgs.length == 0)
            return new ItemText("translate", key, ItemText.createModifiers(modifiers));

        // Fill translate arguments (if any)
        StringBuilder with = new StringBuilder(",\"with\":[");
        int idx = 0;
        for (ItemText arg : jsonArgs)
        {
            if (idx > 0)
            {
                with.append(",");
            }
            with.append(arg.asJsonString());
            idx += 1;
        }
        return new ItemText("translate", key, with.append("]").append(ItemText.createModifiers(modifiers)).toString());
    }

    private static String advancementKey(@NonNull Advancement advancement)
    {
        String result = advancement.getKey().getKey().replace("/", ".");
        result = switch (result) // Needed to correct Spigot on some advancement names vs how they appear in the lang files
        {
            case "husbandry.obtain_netherite_hoe" -> "husbandry.netherite_hoe";
            case "husbandry.bred_all_animals" -> "husbandry.breed_all_animals";
            default -> result;
        };
        return "advancements." + result;
    }

    private static String statisticKey(Statistic statistic)
    {
        String prefix = statistic.isSubstatistic() ? "stat_type.minecraft." : "stat.minecraft.";
        String result = statTranslation.getConfig().getString(statistic.name(), "");
        return !result.equals("") ? prefix + result : statistic.name();
    }

    private static String itemKey(Material item)
    {
        return (item.isBlock() ? "block" : "item") + ".minecraft." + item.getKey().getKey();
    }

    private static String entityKey(EntityType entity)
    {
        return switch (entity)
                {
                    case MUSHROOM_COW -> "entity.minecraft.mooshroom";
                    case SNOWMAN -> "entity.minecraft.snow_golem";
                    default -> "entity.minecraft." + entity.name().toLowerCase();
                };
    }
}
