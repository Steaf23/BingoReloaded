package io.github.steaf23.easymenulib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatComponentUtils
{
    public static Component[] createComponentsFromString(String... strings) {
        return Arrays.stream(strings).map(s -> LegacyComponentSerializer.legacySection().deserialize(s)).toList().toArray(new Component[]{});
    }

    public static Component itemName(Material item)
    {
        return Component.translatable(itemKey(item));
    }

    public static Component advancementTitle(@NotNull Advancement advancement) {
        return Component.translatable(advancementKey(advancement) + ".description");
    }

    public static Component advancementDescription(@NotNull Advancement advancement) {
        return Component.translatable(advancementKey(advancement) + ".description");
    }

    public static Component statistic(Statistic statistic, Component... with)
    {
        return Component.translatable(statisticKey(statistic), with);
    }

    public static Component entityName(EntityType entity)
    {
        return Component.translatable(entityKey(entity));
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
