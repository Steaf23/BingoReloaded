package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.util.StatisticsKeyConverter;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StatisticTypePaper implements StatisticType {

	public static Map<Statistic, StatisticCategory> STATISTIC_CATEGORY_MAP = makeStatisticMap();

	private static Map<Statistic, StatisticCategory> makeStatisticMap() {
		Map<Statistic, StatisticCategory> map = new HashMap<>();
		for (var stat : Statistic.values()) {
			map.put(stat, new StatisticTypePaper(stat).getCategory());
		}
		return map;
	}

	private final Statistic stat;

	public StatisticTypePaper(Statistic stat) {
		this.stat = stat;
	}

	public Statistic handle() {
		return stat;
	}

	@Override
	public @NotNull Key key() {
		return stat.key();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StatisticTypePaper other) {
			return stat.equals(other.stat);
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(stat);
	}

	@Override
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
				 DAMAGE_BLOCKED_BY_SHIELD
					-> StatisticCategory.DAMAGE;

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
				 CLEAN_SHULKER_BOX
					-> StatisticCategory.OTHER;

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
				 INTERACT_WITH_SMITHING_TABLE
					-> StatisticCategory.BLOCK_INTERACT;

			case OPEN_BARREL,
				 CHEST_OPENED,
				 ENDERCHEST_OPENED,
				 SHULKER_BOX_OPENED,
				 TRAPPED_CHEST_TRIGGERED,
				 HOPPER_INSPECTED,
				 DROPPER_INSPECTED,
				 DISPENSER_INSPECTED
					-> StatisticCategory.CONTAINER_INTERACT;

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
				 HAPPY_GHAST_ONE_CM,
				 NAUTILUS_ONE_CM
					-> StatisticCategory.TRAVEL;
		};
	}

	@Override
	public boolean isSubStatistic() {
		return stat.isSubstatistic();
	}

	@Override
	public String translationKey() {
		return StatisticsKeyConverter.getMinecraftTranslationKey(stat);
	}

	/**
	 * @return True if this type is processed by the PlayerStatisticIncrementEvent
	 */
	@Override
	public boolean getsUpdatedAutomatically()
	{
		if (getCategory() == StatisticType.StatisticCategory.TRAVEL)
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

	@Override
	public @NotNull ItemType icon(StatisticDefinition fullStatistic)
	{
		return switch (stat)
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
			case NAUTILUS_ONE_CM -> ItemTypePaper.of(Material.GOLDEN_NAUTILUS_ARMOR);
			case DROP,
				 PICKUP,
				 MINE_BLOCK,
				 USE_ITEM,
				 BREAK_ITEM,
				 CRAFT_ITEM,
				 KILL_ENTITY,
				 ENTITY_KILLED_BY -> rootStatIcon(stat, fullStatistic.itemType(), fullStatistic.entityType());
		};
	}


	private ItemType rootStatIcon(Statistic statistic, @Nullable ItemType item, @Nullable EntityType entity)
	{
		if (statistic.getType() == Statistic.Type.ITEM || statistic.getType() == Statistic.Type.BLOCK) {
			return item;
		}
		else if (entity != null &&
				statistic.getType() == Statistic.Type.ENTITY)
		{
			return ItemTypePaper.of(Registry.MATERIAL.get(Key.key("minecraft:" + entity.key().value() + "_spawn_egg")));
		}

		return ItemTypePaper.of(Material.GLOBE_BANNER_PATTERN);
	}

}
