package io.github.steaf23.easymenulib.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatComponentUtils
{
    public static BaseComponent[] createComponentsFromString(String... strings) {
        return Arrays.stream(strings).map( a -> {
            return ChatComponentUtils.concatComponents(TextComponent.fromLegacyText(a));
        }).toList().toArray(new BaseComponent[]{});
    }

    public static @NotNull ItemStack itemStackFromComponent(@NotNull Material material, @Nullable BaseComponent name, BaseComponent... lore) {
        StringBuilder jsonData = new StringBuilder(material.toString().toLowerCase());
        jsonData.append("{display:{");
        if (name != null) {
            BaseComponent nameWrapper = new TextComponent();
            nameWrapper.setItalic(false);
            nameWrapper.addExtra(name);
            jsonData.append("Name:'[" + ChatComponentUtils.toJsonString(nameWrapper) + "]'");
        }

        if (lore.length == 0 || (lore.length == 1 && lore[0].toPlainText().isEmpty()))
            return Bukkit.getItemFactory().createItemStack(jsonData.append("}}").toString());

        if (name != null) {
            jsonData.append(",");
        }
        jsonData.append("Lore:[");
        for (int i = 0; i < lore.length; i ++)
        {
            if (i != 0) {
                jsonData.append(",");
            }

            BaseComponent lineWrapper = new TextComponent();
            lineWrapper.setItalic(false);
            lineWrapper.addExtra(lore[i]);
            jsonData.append("'")
                    .append(ChatComponentUtils.toJsonString(lineWrapper))
                    .append("'");
        }
        jsonData.append("]}}");

        return Bukkit.getItemFactory().createItemStack(jsonData.toString());
    }

    public static String toJsonString(BaseComponent component) {
        return ComponentSerializer.toString(component).replace("\\u", "\\\\u");
    }

    public static BaseComponent convert(String text, ChatColor... modifiers) {
        BaseComponent component = ChatComponentUtils.concatComponents(TextComponent.fromLegacyText(text));
        return ChatComponentUtils.modify(component, modifiers);
    }

    public static BaseComponent modify(BaseComponent component, ChatColor... modifiers) {
        Set<ChatColor> modifierSet = new HashSet<>(Arrays.stream(modifiers).collect(Collectors.toList()));
        for (ChatColor mod : modifierSet)
        {
            if (mod.getColor() != null)
            {
                component.setColor(mod);
                break;
            }
        }
        if (modifierSet.contains(ChatColor.ITALIC)) component.setItalic(true);
        if (modifierSet.contains(ChatColor.BOLD)) component.setBold(true);
        if (modifierSet.contains(ChatColor.STRIKETHROUGH)) component.setStrikethrough(true);
        if (modifierSet.contains(ChatColor.UNDERLINE)) component.setUnderlined(true);
        if (modifierSet.contains(ChatColor.MAGIC)) component.setObfuscated(true);
        return component;
    }

    public static ComponentBuilder formattedBuilder(ChatColor... modifiers) {
        ComponentBuilder builder = new ComponentBuilder();
        Set<ChatColor> modifierSet = new HashSet<>(Arrays.stream(modifiers).collect(Collectors.toList()));
        for (ChatColor mod : modifierSet)
        {
            if (mod.getColor() != null)
            {
                builder.color(mod);
                break;
            }
        }
        if (modifierSet.contains(ChatColor.ITALIC)) builder.italic(true);
        if (modifierSet.contains(ChatColor.BOLD)) builder.bold(true);
        if (modifierSet.contains(ChatColor.STRIKETHROUGH)) builder.strikethrough(true);
        if (modifierSet.contains(ChatColor.UNDERLINE)) builder.underlined(true);
        if (modifierSet.contains(ChatColor.MAGIC)) builder.obfuscated(true);
        return builder;
    }

    public static BaseComponent concatComponents(BaseComponent... components) {
        BaseComponent result = new TextComponent();
        result.setExtra(Arrays.stream(components).toList());
        return result;
    }

    public static BaseComponent itemName(Material item)
    {
        return new TranslatableComponent(itemKey(item));
    }

    public static BaseComponent advancementTitle(@NotNull Advancement advancement) {
        return new TranslatableComponent(advancementKey(advancement) + ".title");
    }

    public static BaseComponent advancementDescription(@NotNull Advancement advancement) {
        return new TranslatableComponent(advancementKey(advancement) + ".description");
    }

    public static BaseComponent statistic(Statistic statistic, BaseComponent... with)
    {
        return new TranslatableComponent(statisticKey(statistic), with);
    }

    public static BaseComponent entityName(EntityType entity)
    {
        return new TranslatableComponent(entityKey(entity));
    }

    public static BaseComponent smallCaps(String text) {
        return new TextComponent(SmallCaps.toSmallCaps(text));
    }

    private static String advancementKey(@NotNull Advancement advancement)
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
        String result = StatisticsKeyConverter.getMinecraftTranslationKey(statistic);
        return !result.equals("") ? prefix + result : statistic.name();
    }

    private static String itemKey(Material item)
    {
        return (item.isBlock() ? "block" : "item") + ".minecraft." + item.getKey().getKey();
    }

    private static String entityKey(EntityType entity)
    {
        // Note: before 1.20.6 , mooshroom and snow_golem needed to be translated manually.
        return "entity.minecraft." + entity.name().toLowerCase();
    }
}
