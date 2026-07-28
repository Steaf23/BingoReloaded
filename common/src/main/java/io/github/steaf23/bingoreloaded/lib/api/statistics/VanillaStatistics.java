package io.github.steaf23.bingoreloaded.lib.api.statistics;

import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItem;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class VanillaStatistics {
	public static final Map<VanillaStatistic.Category, List<VanillaStatistic>> STATISTICS_BY_CATEGORY = new HashMap<>();

	public static final VanillaStatistic ANIMALS_BRED = register("animals_bred", VanillaStatistic.Category.OTHER, VanillaItems.WHEAT);
	public static final VanillaStatistic AVIATE_ONE_CM = register("aviate_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.ELYTRA);
	public static final VanillaStatistic BELL_RING = register("bell_ring", VanillaStatistic.Category.OTHER, VanillaItems.BELL);
	public static final VanillaStatistic BOAT_ONE_CM = register("boat_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.OAK_BOAT);
	public static final VanillaStatistic ARMOR_CLEANED = register("armor_cleaned", VanillaStatistic.Category.OTHER, VanillaItems.LEATHER_CHESTPLATE);
	public static final VanillaStatistic BANNER_CLEANED = register("banner_cleaned", VanillaStatistic.Category.OTHER, VanillaItems.WHITE_BANNER);
	public static final VanillaStatistic CLEAN_SHULKER_BOX = register("clean_shulker_box", VanillaStatistic.Category.OTHER, VanillaItems.SHULKER_SHELL);
	public static final VanillaStatistic CLIMB_ONE_CM = register("climb_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.EMERALD_ORE);
	public static final VanillaStatistic CROUCH_ONE_CM = register("crouch_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.SCULK_SENSOR);
	public static final VanillaStatistic DAMAGE_ABSORBED = register("damage_absorbed", VanillaStatistic.Category.DAMAGE, VanillaItems.SPONGE);
	public static final VanillaStatistic DAMAGE_BLOCKED_BY_SHIELD = register("damage_blocked_by_shield", VanillaStatistic.Category.DAMAGE, VanillaItems.SHIELD);
	public static final VanillaStatistic DAMAGE_DEALT = register("damage_dealt", VanillaStatistic.Category.DAMAGE, VanillaItems.DIAMOND_SWORD);
	public static final VanillaStatistic DAMAGE_DEALT_ABSORBED = register("damage_dealt_absorbed", VanillaStatistic.Category.DAMAGE, VanillaItems.DAMAGED_ANVIL);
	public static final VanillaStatistic DAMAGE_DEALT_RESISTED = register("damage_dealt_resisted", VanillaStatistic.Category.DAMAGE, VanillaItems.NETHERITE_SWORD);
	public static final VanillaStatistic DAMAGE_RESISTED = register("damage_resisted", VanillaStatistic.Category.DAMAGE, VanillaItems.DIAMOND_CHESTPLATE);
	public static final VanillaStatistic DAMAGE_TAKEN = register("damage_taken", VanillaStatistic.Category.DAMAGE, VanillaItems.IRON_CHESTPLATE);
	public static final VanillaStatistic DEATHS = register("deaths", VanillaStatistic.Category.OTHER, VanillaItems.SKELETON_SKULL);
	public static final VanillaStatistic DROP_COUNT = register("drop_count", VanillaStatistic.Category.OTHER, VanillaItems.HOPPER);
	public static final VanillaStatistic CAKE_SLICES_EATEN = register("cake_slices_eaten", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.CAKE);
	public static final VanillaStatistic ITEM_ENCHANTED = register("item_enchanted", VanillaStatistic.Category.OTHER, VanillaItems.ENCHANTING_TABLE);
	public static final VanillaStatistic FALL_ONE_CM = register("fall_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.WATER_BUCKET);
	public static final VanillaStatistic CAULDRON_FILLED = register("cauldron_filled", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.CAULDRON);
	public static final VanillaStatistic FISH_CAUGHT = register("fish_caught", VanillaStatistic.Category.OTHER, VanillaItems.TROPICAL_FISH);
	public static final VanillaStatistic FLY_ONE_CM = register("fly_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.COMMAND_BLOCK);
	public static final VanillaStatistic HAPPY_GHAST_ONE_CM = register("happy_ghast_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.DRIED_GHAST);
	public static final VanillaStatistic HORSE_ONE_CM = register("horse_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.SADDLE);
	public static final VanillaStatistic DISPENSER_INSPECTED = register("dispenser_inspected", VanillaStatistic.Category.CONTAINER_INTERACT, VanillaItems.DISPENSER);
	public static final VanillaStatistic DROPPER_INSPECTED = register("dropper_inspected", VanillaStatistic.Category.CONTAINER_INTERACT, VanillaItems.DROPPER);
	public static final VanillaStatistic HOPPER_INSPECTED = register("hopper_inspected", VanillaStatistic.Category.CONTAINER_INTERACT, VanillaItems.HOPPER);
	public static final VanillaStatistic INTERACT_WITH_ANVIL = register("interact_with_anvil", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.ANVIL);
	public static final VanillaStatistic BEACON_INTERACTION = register("beacon_interaction", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.BEACON);
	public static final VanillaStatistic INTERACT_WITH_BLAST_FURNACE = register("interact_with_blast_furnace", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.BLAST_FURNACE);
	public static final VanillaStatistic BREWINGSTAND_INTERACTION = register("brewingstand_interaction", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.BREWING_STAND);
	public static final VanillaStatistic INTERACT_WITH_CAMPFIRE = register("interact_with_campfire", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.CAMPFIRE);
	public static final VanillaStatistic INTERACT_WITH_CARTOGRAPHY_TABLE = register("interact_with_cartography_table", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.CARTOGRAPHY_TABLE);
	public static final VanillaStatistic CRAFTING_TABLE_INTERACTION = register("crafting_table_interaction", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.CRAFTING_TABLE);
	public static final VanillaStatistic FURNACE_INTERACTION = register("furnace_interaction", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.FURNACE);
	public static final VanillaStatistic INTERACT_WITH_GRINDSTONE = register("interact_with_grindstone", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.GRINDSTONE);
	public static final VanillaStatistic INTERACT_WITH_LECTERN = register("interact_with_lectern", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.LECTERN);
	public static final VanillaStatistic INTERACT_WITH_LOOM = register("interact_with_loom", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.LOOM);
	public static final VanillaStatistic INTERACT_WITH_SMITHING_TABLE = register("interact_with_smithing_table", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.SMITHING_TABLE);
	public static final VanillaStatistic INTERACT_WITH_SMOKER = register("interact_with_smoker", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.SMOKER);
	public static final VanillaStatistic INTERACT_WITH_STONECUTTER = register("interact_with_stonecutter", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.STONECUTTER);
	public static final VanillaStatistic JUMP = register("jump", VanillaStatistic.Category.OTHER, VanillaItems.RABBIT_FOOT);
	public static final VanillaStatistic LEAVE_GAME = register("leave_game", VanillaStatistic.Category.OTHER, VanillaItems.OAK_DOOR);
	public static final VanillaStatistic MINECART_ONE_CM = register("minecart_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.MINECART);
	public static final VanillaStatistic MOB_KILLS = register("mob_kills", VanillaStatistic.Category.OTHER, VanillaItems.CREEPER_HEAD);
	public static final VanillaStatistic NAUTILUS_ONE_CM = register("nautilus_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.NAUTILUS_SHELL);
	public static final VanillaStatistic OPEN_BARREL = register("open_barrel", VanillaStatistic.Category.CONTAINER_INTERACT, VanillaItems.BARREL);
	public static final VanillaStatistic CHEST_OPENED = register("chest_opened", VanillaStatistic.Category.CONTAINER_INTERACT, VanillaItems.CHEST);
	public static final VanillaStatistic ENDERCHEST_OPENED = register("enderchest_opened", VanillaStatistic.Category.CONTAINER_INTERACT, VanillaItems.ENDER_CHEST);
	public static final VanillaStatistic SHULKER_BOX_OPENED = register("shulker_box_opened", VanillaStatistic.Category.CONTAINER_INTERACT, VanillaItems.SHULKER_BOX);
	public static final VanillaStatistic PIG_ONE_CM = register("pig_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.CARROT_ON_A_STICK);
	public static final VanillaStatistic NOTEBLOCK_PLAYED = register("noteblock_played", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.NOTE_BLOCK);
	public static final VanillaStatistic RECORD_PLAYED = register("record_played", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.JUKEBOX);
	public static final VanillaStatistic PLAY_ONE_MINUTE = register("play_one_minute", VanillaStatistic.Category.OTHER, VanillaItems.CLOCK);
	public static final VanillaStatistic PLAYER_KILLS = register("player_kills", VanillaStatistic.Category.OTHER, VanillaItems.PLAYER_HEAD);
	public static final VanillaStatistic FLOWER_POTTED = register("flower_potted", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.FLOWER_POT);
	public static final VanillaStatistic RAID_TRIGGER = register("raid_trigger", VanillaStatistic.Category.OTHER, VanillaItems.OMINOUS_BOTTLE);
	public static final VanillaStatistic RAID_WIN = register("raid_win", VanillaStatistic.Category.OTHER, VanillaItems.TOTEM_OF_UNDYING);
	public static final VanillaStatistic SLEEP_IN_BED = register("sleep_in_bed", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.RED_BED);
	public static final VanillaStatistic SNEAK_TIME = register("sneak_time", VanillaStatistic.Category.OTHER, VanillaItems.SCULK_SHRIEKER);
	public static final VanillaStatistic SPRINT_ONE_CM = register("sprint_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.FEATHER);
	public static final VanillaStatistic STRIDER_ONE_CM = register("strider_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.WARPED_FUNGUS_ON_A_STICK);
	public static final VanillaStatistic SWIM_ONE_CM = register("swim_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.BUBBLE_CORAL);
	public static final VanillaStatistic TALKED_TO_VILLAGER = register("talked_to_villager", VanillaStatistic.Category.OTHER, VanillaItems.POPPY);
	public static final VanillaStatistic TARGET_HIT = register("target_hit", VanillaStatistic.Category.OTHER, VanillaItems.TARGET);
	public static final VanillaStatistic TIME_SINCE_DEATH = register("time_since_death", VanillaStatistic.Category.OTHER, VanillaItems.RECOVERY_COMPASS);
	public static final VanillaStatistic TIME_SINCE_REST = register("time_since_rest", VanillaStatistic.Category.OTHER, VanillaItems.YELLOW_BED);
	public static final VanillaStatistic TOTAL_WORLD_TIME = register("total_world_time", VanillaStatistic.Category.OTHER, VanillaItems.FILLED_MAP);
	public static final VanillaStatistic TRADED_WITH_VILLAGER = register("traded_with_villager", VanillaStatistic.Category.OTHER, VanillaItems.EMERALD);
	public static final VanillaStatistic TRAPPED_CHEST_TRIGGERED = register("trapped_chest_triggered", VanillaStatistic.Category.CONTAINER_INTERACT, VanillaItems.TRAPPED_CHEST);
	public static final VanillaStatistic NOTEBLOCK_TUNED = register("noteblock_tuned", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.NOTE_BLOCK);
	public static final VanillaStatistic CAULDRON_USED = register("cauldron_used", VanillaStatistic.Category.BLOCK_INTERACT, VanillaItems.CAULDRON);
	public static final VanillaStatistic WALK_ON_WATER_ONE_CM = register("walk_on_water_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.ICE);
	public static final VanillaStatistic WALK_ONE_CM = register("walk_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.LEATHER_BOOTS);
	public static final VanillaStatistic WALK_UNDER_WATER_ONE_CM = register("walk_under_water_one_cm", VanillaStatistic.Category.TRAVEL, VanillaItems.GOLDEN_BOOTS);
	public static final VanillaStatistic BREAK_ITEM = register("break_item", VanillaStatistic.Category.ROOT_STATISTIC, VanillaStatistics::iconFromItem);
	public static final VanillaStatistic CRAFT_ITEM = register("craft_item", VanillaStatistic.Category.ROOT_STATISTIC, VanillaStatistics::iconFromItem);
	public static final VanillaStatistic DROP = register("drop", VanillaStatistic.Category.ROOT_STATISTIC, VanillaStatistics::iconFromItem);
	public static final VanillaStatistic KILL_ENTITY = register("kill_entity", VanillaStatistic.Category.ROOT_STATISTIC, VanillaStatistics::iconFromEntity);
	public static final VanillaStatistic ENTITY_KILLED_BY = register("entity_killed_by", VanillaStatistic.Category.ROOT_STATISTIC, VanillaStatistics::iconFromEntity);
	public static final VanillaStatistic MINE_BLOCK = register("mine_block", VanillaStatistic.Category.ROOT_STATISTIC, VanillaStatistics::iconFromItem);
	public static final VanillaStatistic PICKUP = register("pickup", VanillaStatistic.Category.ROOT_STATISTIC, VanillaStatistics::iconFromItem);
	public static final VanillaStatistic USE_ITEM = register("use_item", VanillaStatistic.Category.ROOT_STATISTIC, VanillaStatistics::iconFromItem);

	public static @Nullable VanillaStatistic fromKey(Key key) {
		String compare = key.value();
		for (VanillaStatistic.Category cat : STATISTICS_BY_CATEGORY.keySet()) {
			for (VanillaStatistic stat : STATISTICS_BY_CATEGORY.get(cat)) {
				if (compare.equals(stat.keyStr())) {
					return stat;
				}
			}
		}

		return null;
	}

	private static ItemType iconFromItem(StatisticHandle handle) {
		return handle.itemType() == null ? VanillaItems.GLOBE_BANNER_PATTERN.type() : handle.itemType();
	}

	private static ItemType iconFromEntity(StatisticHandle handle) {
		EntityType type = handle.entityType();
		if (type == null ) {
			return VanillaItems.GLOBE_BANNER_PATTERN.type();
		}
		return ItemType.of("minecraft:" + type.key().value() + "_spawn_egg");
	}

	private static VanillaStatistic register(String id, VanillaStatistic.Category category, Function<StatisticHandle, ItemType> icon) {
		VanillaStatistic stat = new VanillaStatistic(id, category, icon);
		STATISTICS_BY_CATEGORY.putIfAbsent(category, new ArrayList<>());
		STATISTICS_BY_CATEGORY.get(category).add(stat);
		return stat;
	}

	private static VanillaStatistic register(String id, VanillaStatistic.Category category, VanillaItem icon) {
		return VanillaStatistics.register(id, category, i -> icon.type());
	}


}
