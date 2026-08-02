package io.github.steaf23.bingoreloaded.lib.util;

import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;

public class ComponentUtils
{
    public static final MiniMessage MINI_BUILDER = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolvers(StandardTags.defaults(), TinyCaps.TAG_RESOLVER)
                    .build())
            .build();

    public static Component[] createComponentsFromString(String... strings) {
        return Arrays.stream(strings).map(s -> LegacyComponentSerializer.legacySection().deserialize(s)).toList().toArray(new Component[]{});
    }

    public static Component itemName(ItemType item)
    {
        return Component.translatable(itemKey(item));
    }

    public static Component statistic(StatisticHandle statistic, Component... with)
    {
        return Component.translatable(statisticKey(statistic), with);
    }

    public static Component entityName(EntityType entity)
    {
        return Component.translatable(entityKey(entity));
    }

    private static String statisticKey(StatisticHandle statistic)
    {
        String prefix = statistic.isSubStatistic() ? "stat_type.minecraft." : "stat.minecraft.";
        String result = statistic.translationKey();
        return !result.isEmpty() ? prefix + result : statistic.statisticType().key().asString();
    }

    private static String itemKey(ItemType item)
    {
        return (item.isBlock() ? "block" : "item") + ".minecraft." + item.key().value();
    }

    private static String entityKey(EntityType entity)
    {
        // Note: before 1.20.6 , mooshroom and snow_golem needed to be translated manually.
        return "entity.minecraft." + entity.key().value();
    }
}
