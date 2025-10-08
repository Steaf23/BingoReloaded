package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.util.StatisticsKeyConverter;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record StatisticHandlePaper(@NotNull StatisticTypePaper statistic, @Nullable EntityType entityType, @Nullable ItemType itemType) implements StatisticHandle
{
    public StatisticHandlePaper(StatisticTypePaper stat)
    {
        this(stat, null, null);
    }

    public StatisticHandlePaper(StatisticTypePaper stat, @Nullable EntityType entityType)
    {
        this(stat, entityType, null);
    }

    public StatisticHandlePaper(StatisticTypePaper stat, @Nullable ItemType itemType)
    {
        this(stat, null, itemType);
    }

    public StatisticHandlePaper(Statistic stat)
    {
        this(new StatisticTypePaper(stat), null, null);
    }

    public StatisticHandlePaper(Statistic stat, @NotNull org.bukkit.entity.EntityType entityType)
    {
        this(new StatisticTypePaper(stat), new EntityTypePaper(entityType), null);
    }

    public StatisticHandlePaper(Statistic stat, @NotNull Material itemType)
    {
        this(new StatisticTypePaper(stat), null, ItemTypePaper.of(itemType));
    }

    public static StatisticHandlePaper create(Statistic stat, @Nullable org.bukkit.entity.EntityType entity, @Nullable Material itemType) {
        if (entity == null && itemType != null) {
            return new StatisticHandlePaper(new StatisticTypePaper(stat), null, ItemTypePaper.of(itemType));
        } else if (entity != null && itemType == null) {
            return new StatisticHandlePaper(new StatisticTypePaper(stat), new EntityTypePaper(entity), null);
        } else if (entity == null && itemType == null) {
            return new StatisticHandlePaper(new StatisticTypePaper(stat), null, null);
        } else {
            return new StatisticHandlePaper(new StatisticTypePaper(stat), new EntityTypePaper(entity), ItemTypePaper.of(itemType));
        }
    }

    @Override
    public StatisticType statisticType() {
        return statistic;
    }

    @Override
    public boolean isSubStatistic() {
        return statistic.handle().isSubstatistic();
    }

    @Override
    public String translationKey() {
        return StatisticsKeyConverter.getMinecraftTranslationKey(statistic.handle());
    }

    /**
     * @return True if this statistic is processed by the PlayerStatisticIncrementEvent
     */
    public boolean getsUpdatedAutomatically()
    {
        if (statistic.getCategory() == StatisticType.StatisticCategory.TRAVEL)
            return false;

        return switch (statistic.handle())
        {
            case PLAY_ONE_MINUTE,
                    SNEAK_TIME,
                    TOTAL_WORLD_TIME,
                    TIME_SINCE_REST,
                    TIME_SINCE_DEATH -> false;
            default -> true;
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
        return switch (statistic.handle())
        {
            case DAMAGE_DEALT -> ItemTypePaper.of(Material.DIAMOND_SWORD);
            case DAMAGE_TAKEN -> ItemTypePaper.of(Material.IRON_CHESTPLATE);
            case DEATHS -> ItemTypePaper.of(Material.SKELETON_SKULL);
            case MOB_KILLS -> ItemTypePaper.of(Material.CREEPER_HEAD);
            case PLAYER_KILLS -> ItemTypePaper.of(Material.PLAYER_HEAD);
            case FISH_CAUGHT -> ItemTypePaper.of(Material.TROPICAL_FISH);
            case ANIMALS_BRED -> ItemTypePaper.of(Material.WHEAT);
            case LEAVE_GAME -> ItemTypePaper.of(Material.BARRIER);
            case JUMP -> ItemTypePaper.of(Material.RABBIT_FOOT);
            case DROP_COUNT, HOPPER_INSPECTED -> ItemTypePaper.of(Material.HOPPER);
            case PLAY_ONE_MINUTE -> ItemTypePaper.of(Material.CLOCK);
            case TOTAL_WORLD_TIME -> ItemTypePaper.of(Material.FILLED_MAP);
            case WALK_ONE_CM -> ItemTypePaper.of(Material.LEATHER_BOOTS);
            case WALK_ON_WATER_ONE_CM -> ItemTypePaper.of(Material.ICE);
            case FALL_ONE_CM -> ItemTypePaper.of(Material.LAVA_BUCKET);
            case SNEAK_TIME -> ItemTypePaper.of(Material.SCULK_SHRIEKER);
            case CLIMB_ONE_CM -> ItemTypePaper.of(Material.EMERALD_ORE);
            case FLY_ONE_CM -> ItemTypePaper.of(Material.COMMAND_BLOCK);
            case WALK_UNDER_WATER_ONE_CM -> ItemTypePaper.of(Material.GOLDEN_BOOTS);
            case MINECART_ONE_CM -> ItemTypePaper.of(Material.MINECART);
            case BOAT_ONE_CM -> ItemTypePaper.of(Material.OAK_BOAT);
            case PIG_ONE_CM -> ItemTypePaper.of(Material.CARROT_ON_A_STICK);
            case HORSE_ONE_CM -> ItemTypePaper.of(Material.SADDLE);
            case SPRINT_ONE_CM -> ItemTypePaper.of(Material.FEATHER);
            case CROUCH_ONE_CM -> ItemTypePaper.of(Material.SCULK_SENSOR);
            case AVIATE_ONE_CM -> ItemTypePaper.of(Material.ELYTRA);
            case TIME_SINCE_DEATH -> ItemTypePaper.of(Material.RECOVERY_COMPASS);
            case TALKED_TO_VILLAGER -> ItemTypePaper.of(Material.POPPY);
            case TRADED_WITH_VILLAGER -> ItemTypePaper.of(Material.EMERALD);
            case CAKE_SLICES_EATEN -> ItemTypePaper.of(Material.CAKE);
            case CAULDRON_FILLED -> ItemTypePaper.of(Material.CAULDRON);
            case CAULDRON_USED -> ItemTypePaper.of(Material.WATER_BUCKET);
            case ARMOR_CLEANED -> ItemTypePaper.of(Material.LEATHER_CHESTPLATE);
            case BANNER_CLEANED -> ItemTypePaper.of(Material.WHITE_BANNER);
            case BREWINGSTAND_INTERACTION -> ItemTypePaper.of(Material.BREWING_STAND);
            case BEACON_INTERACTION -> ItemTypePaper.of(Material.BEACON);
            case DROPPER_INSPECTED -> ItemTypePaper.of(Material.DROPPER);
            case DISPENSER_INSPECTED -> ItemTypePaper.of(Material.DISPENSER);
            case NOTEBLOCK_PLAYED, NOTEBLOCK_TUNED -> ItemTypePaper.of(Material.NOTE_BLOCK);
            case FLOWER_POTTED -> ItemTypePaper.of(Material.FLOWER_POT);
            case TRAPPED_CHEST_TRIGGERED -> ItemTypePaper.of(Material.TRAPPED_CHEST);
            case ENDERCHEST_OPENED -> ItemTypePaper.of(Material.ENDER_CHEST);
            case ITEM_ENCHANTED -> ItemTypePaper.of(Material.ENCHANTING_TABLE);
            case RECORD_PLAYED -> ItemTypePaper.of(Material.MUSIC_DISC_CAT);
            case FURNACE_INTERACTION -> ItemTypePaper.of(Material.FURNACE);
            case CRAFTING_TABLE_INTERACTION -> ItemTypePaper.of(Material.CRAFTING_TABLE);
            case CHEST_OPENED -> ItemTypePaper.of(Material.CHEST);
            case SLEEP_IN_BED -> ItemTypePaper.of(Material.RED_BED);
            case SHULKER_BOX_OPENED -> ItemTypePaper.of(Material.SHULKER_BOX);
            case TIME_SINCE_REST -> ItemTypePaper.of(Material.YELLOW_BED);
            case SWIM_ONE_CM -> ItemTypePaper.of(Material.BUBBLE_CORAL);
            case DAMAGE_DEALT_ABSORBED -> ItemTypePaper.of(Material.DAMAGED_ANVIL);
            case DAMAGE_DEALT_RESISTED -> ItemTypePaper.of(Material.NETHERITE_SWORD);
            case DAMAGE_BLOCKED_BY_SHIELD -> ItemTypePaper.of(Material.SHIELD);
            case DAMAGE_ABSORBED -> ItemTypePaper.of(Material.SPONGE);
            case DAMAGE_RESISTED -> ItemTypePaper.of(Material.DIAMOND_CHESTPLATE);
            case CLEAN_SHULKER_BOX -> ItemTypePaper.of(Material.SHULKER_SHELL);
            case OPEN_BARREL -> ItemTypePaper.of(Material.BARREL);
            case INTERACT_WITH_BLAST_FURNACE -> ItemTypePaper.of(Material.BLAST_FURNACE);
            case INTERACT_WITH_SMOKER -> ItemTypePaper.of(Material.SMOKER);
            case INTERACT_WITH_LECTERN -> ItemTypePaper.of(Material.LECTERN);
            case INTERACT_WITH_CAMPFIRE -> ItemTypePaper.of(Material.CAMPFIRE);
            case INTERACT_WITH_CARTOGRAPHY_TABLE -> ItemTypePaper.of(Material.CARTOGRAPHY_TABLE);
            case INTERACT_WITH_LOOM -> ItemTypePaper.of(Material.LOOM);
            case INTERACT_WITH_STONECUTTER -> ItemTypePaper.of(Material.STONECUTTER);
            case BELL_RING -> ItemTypePaper.of(Material.BELL);
            case RAID_TRIGGER -> ItemTypePaper.of(Material.CROSSBOW);
            case RAID_WIN -> ItemTypePaper.of(Material.TOTEM_OF_UNDYING);
            case INTERACT_WITH_ANVIL -> ItemTypePaper.of(Material.ANVIL);
            case INTERACT_WITH_GRINDSTONE -> ItemTypePaper.of(Material.GRINDSTONE);
            case TARGET_HIT -> ItemTypePaper.of(Material.TARGET);
            case INTERACT_WITH_SMITHING_TABLE -> ItemTypePaper.of(Material.SMITHING_TABLE);
            case STRIDER_ONE_CM -> ItemTypePaper.of(Material.WARPED_FUNGUS_ON_A_STICK);
            case HAPPY_GHAST_ONE_CM -> ItemTypePaper.of(Material.DRIED_GHAST);
            case DROP,
                    PICKUP,
                    MINE_BLOCK,
                    USE_ITEM,
                    BREAK_ITEM,
                    CRAFT_ITEM,
                    KILL_ENTITY,
                    ENTITY_KILLED_BY -> rootStatIcon(statistic.handle());
        };
    }

    private ItemType rootStatIcon(Statistic statistic)
    {
        if (statistic.getType() == Statistic.Type.ITEM || statistic.getType() == Statistic.Type.BLOCK) {
            return itemType();
        }
        else if (entityType() != null &&
                statistic.getType() == Statistic.Type.ENTITY)
        {
            return ItemType.of("minecraft:" + entityType().key().value() + "_spawn_egg");
        }

        return ItemTypePaper.of(Material.GLOBE_BANNER_PATTERN);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StatisticHandlePaper(StatisticTypePaper stat, EntityType entity, ItemType item))) return false;
		return Objects.equals(itemType, item) && Objects.equals(entityType, entity) && Objects.equals(statistic, stat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statistic, entityType, itemType);
    }
}
