package io.github.steaf23.bingoreloaded.lib.api;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record StatisticHandlePaper(@NotNull Statistic stat, @Nullable EntityType entityType, @Nullable ItemType itemType) implements StatisticHandle
{

    public StatisticHandlePaper(Statistic stat)
    {
        this(stat, null, null);
    }

    public StatisticHandlePaper(Statistic stat, @Nullable EntityType entityType)
    {
        this(stat, entityType, null);
    }

    public StatisticHandlePaper(Statistic stat, @Nullable ItemType itemType)
    {
        this(stat, null, itemType);
    }

    //TODO: less static?
    public static List<StatisticType> getStatisticsInCategory(StatisticCategory category)
    {
        List<Statistic> result = new ArrayList<>();
        for (var stat : Statistic.values())
        {
            if (getCategory(stat) == category)
            {
                result.add(stat);
            }
        }
        return result;
    }

    public static boolean isEntityValidForStatistic(EntityType type)
    {
        return validEntityTypes.contains(type);
    }

    @Override
    public @Nullable ItemType item() {
        return itemType;
    }

    @Override
    public @Nullable EntityType entity() {
        return entityType;
    }

    /**
     * @return True if this statistic is processed by the PlayerStatisticIncrementEvent
     */
    public boolean getsUpdatedAutomatically()
    {
        if (getCategory() == StatisticCategory.TRAVEL)
            return false;

        return switch (stat)
        {
            case PLAY_ONE_MINUTE,
                    SNEAK_TIME,
                    TOTAL_WORLD_TIME,
                    TIME_SINCE_REST,
                    TIME_SINCE_DEATH -> false;
            default -> true;
        };
    }

    public StatisticCategory getCategory()
    {
        return switch (stat)
        {
            case DROP,
                    PICKUP,
                    USE_ITEM,
                    BREAK_ITEM,
                    CRAFT_ITEM,
                    KILL_ENTITY,
                    ENTITY_KILLED_BY,
                    MINE_BLOCK -> StatisticCategory.ROOT_STATISTIC;

            case DAMAGE_DEALT,
                    DAMAGE_TAKEN,
                    DAMAGE_DEALT_ABSORBED,
                    DAMAGE_DEALT_RESISTED,
                    DAMAGE_RESISTED,
                    DAMAGE_ABSORBED,
                    DAMAGE_BLOCKED_BY_SHIELD -> StatisticCategory.DAMAGE;

            case TALKED_TO_VILLAGER,
                    TRADED_WITH_VILLAGER,
                    DEATHS,
                    MOB_KILLS,
                    PLAYER_KILLS,
                    FISH_CAUGHT,
                    ANIMALS_BRED,
                    LEAVE_GAME,
                    JUMP,
                    DROP_COUNT,
                    PLAY_ONE_MINUTE,
                    TOTAL_WORLD_TIME,
                    SNEAK_TIME,
                    TIME_SINCE_DEATH,
                    RAID_TRIGGER,
                    ARMOR_CLEANED,
                    BANNER_CLEANED,
                    ITEM_ENCHANTED,
                    TIME_SINCE_REST,
                    RAID_WIN,
                    TARGET_HIT,
                    CLEAN_SHULKER_BOX -> StatisticCategory.OTHER;

            case CAKE_SLICES_EATEN,
                    CAULDRON_FILLED,
                    BREWINGSTAND_INTERACTION,
                    BEACON_INTERACTION,
                    NOTEBLOCK_PLAYED,
                    CAULDRON_USED,
                    NOTEBLOCK_TUNED,
                    FLOWER_POTTED,
                    RECORD_PLAYED,
                    FURNACE_INTERACTION,
                    CRAFTING_TABLE_INTERACTION,
                    SLEEP_IN_BED,
                    INTERACT_WITH_BLAST_FURNACE,
                    INTERACT_WITH_SMOKER,
                    INTERACT_WITH_LECTERN,
                    INTERACT_WITH_CAMPFIRE,
                    INTERACT_WITH_CARTOGRAPHY_TABLE,
                    INTERACT_WITH_LOOM,
                    INTERACT_WITH_STONECUTTER,
                    BELL_RING,
                    INTERACT_WITH_ANVIL,
                    INTERACT_WITH_GRINDSTONE,
                    INTERACT_WITH_SMITHING_TABLE -> StatisticCategory.BLOCK_INTERACT;

            case OPEN_BARREL,
                    CHEST_OPENED,
                    ENDERCHEST_OPENED,
                    SHULKER_BOX_OPENED,
                    TRAPPED_CHEST_TRIGGERED,
                    HOPPER_INSPECTED,
                    DROPPER_INSPECTED,
                    DISPENSER_INSPECTED -> StatisticCategory.CONTAINER_INTERACT;

            case STRIDER_ONE_CM,
                    MINECART_ONE_CM,
                    CLIMB_ONE_CM,
                    FLY_ONE_CM,
                    WALK_UNDER_WATER_ONE_CM,
                    BOAT_ONE_CM,
                    PIG_ONE_CM,
                    HORSE_ONE_CM,
                    CROUCH_ONE_CM,
                    AVIATE_ONE_CM,
                    WALK_ONE_CM,
                    WALK_ON_WATER_ONE_CM,
                    SWIM_ONE_CM,
                    FALL_ONE_CM,
                    SPRINT_ONE_CM,
                    HAPPY_GHAST_ONE_CM -> StatisticCategory.TRAVEL;
        };
    }

    public static String createDescription(Statistic stat)
    {
        return switch (stat)
        {
            default -> "";
        };
    }

    public @NotNull ItemType icon()
    {
        return switch (stat)
        {
            case DAMAGE_DEALT -> ItemType.of("diamond_sword");
            case DAMAGE_TAKEN -> ItemType.of("iron_chestplate");
            case DEATHS -> ItemType.of("skeleton_skull");
            case MOB_KILLS -> ItemType.of("creeper_head");
            case PLAYER_KILLS -> ItemType.of("player_head");
            case FISH_CAUGHT -> ItemType.of("tropical_fish");
            case ANIMALS_BRED -> ItemType.of("wheat");
            case LEAVE_GAME -> ItemType.of("barrier");
            case JUMP -> ItemType.of("rabbit_foot");
            case DROP_COUNT, HOPPER_INSPECTED -> ItemType.of("hopper");
            case PLAY_ONE_MINUTE -> ItemType.of("clock");
            case TOTAL_WORLD_TIME -> ItemType.of("filled_map");
            case WALK_ONE_CM -> ItemType.of("leather_boots");
            case WALK_ON_WATER_ONE_CM -> ItemType.of("ice");
            case FALL_ONE_CM -> ItemType.of("lava_bucket");
            case SNEAK_TIME -> ItemType.of("sculk_shrieker");
            case CLIMB_ONE_CM -> ItemType.of("emerald_ore");
            case FLY_ONE_CM -> ItemType.of("command_block");
            case WALK_UNDER_WATER_ONE_CM -> ItemType.of("golden_boots");
            case MINECART_ONE_CM -> ItemType.of("minecart");
            case BOAT_ONE_CM -> ItemType.of("oak_boat");
            case PIG_ONE_CM -> ItemType.of("carrot_on_a_stick");
            case HORSE_ONE_CM -> ItemType.of("saddle");
            case SPRINT_ONE_CM -> ItemType.of("feather");
            case CROUCH_ONE_CM -> ItemType.of("sculk_sensor");
            case AVIATE_ONE_CM -> ItemType.of("elytra");
            case TIME_SINCE_DEATH -> ItemType.of("recovery_compass");
            case TALKED_TO_VILLAGER -> ItemType.of("poppy");
            case TRADED_WITH_VILLAGER -> ItemType.of("emerald");
            case CAKE_SLICES_EATEN -> ItemType.of("cake");
            case CAULDRON_FILLED -> ItemType.of("cauldron");
            case CAULDRON_USED -> ItemType.of("water_bucket");
            case ARMOR_CLEANED -> ItemType.of("leather_chestplate");
            case BANNER_CLEANED -> ItemType.of("white_banner");
            case BREWINGSTAND_INTERACTION -> ItemType.of("brewing_stand");
            case BEACON_INTERACTION -> ItemType.of("beacon");
            case DROPPER_INSPECTED -> ItemType.of("dropper");
            case DISPENSER_INSPECTED -> ItemType.of("dispenser");
            case NOTEBLOCK_PLAYED, NOTEBLOCK_TUNED -> ItemType.of("note_block");
            case FLOWER_POTTED -> ItemType.of("flower_pot");
            case TRAPPED_CHEST_TRIGGERED -> ItemType.of("trapped_chest");
            case ENDERCHEST_OPENED -> ItemType.of("ender_chest");
            case ITEM_ENCHANTED -> ItemType.of("enchanting_table");
            case RECORD_PLAYED -> ItemType.of("music_disc_cat");
            case FURNACE_INTERACTION -> ItemType.of("furnace");
            case CRAFTING_TABLE_INTERACTION -> ItemType.of("crafting_table");
            case CHEST_OPENED -> ItemType.of("chest");
            case SLEEP_IN_BED -> ItemType.of("red_bed");
            case SHULKER_BOX_OPENED -> ItemType.of("shulker_box");
            case TIME_SINCE_REST -> ItemType.of("yellow_bed");
            case SWIM_ONE_CM -> ItemType.of("bubble_coral");
            case DAMAGE_DEALT_ABSORBED -> ItemType.of("damaged_anvil");
            case DAMAGE_DEALT_RESISTED -> ItemType.of("netherite_sword");
            case DAMAGE_BLOCKED_BY_SHIELD -> ItemType.of("shield");
            case DAMAGE_ABSORBED -> ItemType.of("sponge");
            case DAMAGE_RESISTED -> ItemType.of("diamond_chestplate");
            case CLEAN_SHULKER_BOX -> ItemType.of("shulker_shell");
            case OPEN_BARREL -> ItemType.of("barrel");
            case INTERACT_WITH_BLAST_FURNACE -> ItemType.of("blast_furnace");
            case INTERACT_WITH_SMOKER -> ItemType.of("smoker");
            case INTERACT_WITH_LECTERN -> ItemType.of("lectern");
            case INTERACT_WITH_CAMPFIRE -> ItemType.of("campfire");
            case INTERACT_WITH_CARTOGRAPHY_TABLE -> ItemType.of("cartography_table");
            case INTERACT_WITH_LOOM -> ItemType.of("loom");
            case INTERACT_WITH_STONECUTTER -> ItemType.of("stonecutter");
            case BELL_RING -> ItemType.of("bell");
            case RAID_TRIGGER -> ItemType.of("crossbow");
            case RAID_WIN -> ItemType.of("totem_of_undying");
            case INTERACT_WITH_ANVIL -> ItemType.of("anvil");
            case INTERACT_WITH_GRINDSTONE -> ItemType.of("grindstone");
            case TARGET_HIT -> ItemType.of("target");
            case INTERACT_WITH_SMITHING_TABLE -> ItemType.of("smithing_table");
            case STRIDER_ONE_CM -> ItemType.of("warped_fungus_on_a_stick");
            case HAPPY_GHAST_ONE_CM -> ItemType.of("dried_ghast");
            case DROP,
                    PICKUP,
                    MINE_BLOCK,
                    USE_ITEM,
                    BREAK_ITEM,
                    CRAFT_ITEM,
                    KILL_ENTITY,
                    ENTITY_KILLED_BY -> rootStatItemType(statistic);
        };
    }

    private static ItemType rootStatItemType(StatisticHandlePaper statistic)
    {
        if (statistic.itemType != null &&
                (statistic.stat.getType() == Statistic.Type.ITEM || statistic.stat.getType() == Statistic.Type.BLOCK))
        {
            return statistic.itemType;
        }
        else if (statistic.entityType != null &&
                statistic.stat.getType() == Statistic.Type.ENTITY)
        {
            return ItemType.valueOf(statistic.entityType.name() + "_SPAWN_EGG");
        }

        return ItemType.of("globe_banner_pattern");
    }
}
