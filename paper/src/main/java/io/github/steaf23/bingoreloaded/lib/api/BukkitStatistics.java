package io.github.steaf23.bingoreloaded.lib.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistic;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistics;
import org.bukkit.Statistic;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BukkitStatistics {
	private static final BiMap<Statistic, VanillaStatistic> STATISTICS = createMap();

	public static @Nullable Statistic getBukkitStatistic(VanillaStatistic stat) {
		return STATISTICS.inverse().get(stat);
	}

	public static @Nullable VanillaStatistic getVanillaStatistic(Statistic stat) {
		return STATISTICS.get(stat);
	}

	private static BiMap<Statistic, VanillaStatistic> createMap() {
		BiMap<Statistic, VanillaStatistic> map = HashBiMap.create();
		map.putAll(Map.<Statistic, VanillaStatistic>ofEntries(
				Map.entry(Statistic.ANIMALS_BRED, VanillaStatistics.ANIMALS_BRED),
				Map.entry(Statistic.AVIATE_ONE_CM, VanillaStatistics.AVIATE_ONE_CM),
				Map.entry(Statistic.BELL_RING, VanillaStatistics.BELL_RING),
				Map.entry(Statistic.BOAT_ONE_CM, VanillaStatistics.BOAT_ONE_CM),
				Map.entry(Statistic.ARMOR_CLEANED, VanillaStatistics.CLEAN_ARMOR),
				Map.entry(Statistic.BANNER_CLEANED, VanillaStatistics.CLEAN_BANNER),
				Map.entry(Statistic.CLEAN_SHULKER_BOX, VanillaStatistics.CLEAN_SHULKER_BOX),
				Map.entry(Statistic.CLIMB_ONE_CM, VanillaStatistics.CLIMB_ONE_CM),
				Map.entry(Statistic.CROUCH_ONE_CM, VanillaStatistics.CROUCH_ONE_CM),
				Map.entry(Statistic.DAMAGE_ABSORBED, VanillaStatistics.DAMAGE_ABSORBED),
				Map.entry(Statistic.DAMAGE_BLOCKED_BY_SHIELD, VanillaStatistics.DAMAGE_BLOCKED_BY_SHIELD),
				Map.entry(Statistic.DAMAGE_DEALT, VanillaStatistics.DAMAGE_DEALT),
				Map.entry(Statistic.DAMAGE_DEALT_ABSORBED, VanillaStatistics.DAMAGE_DEALT_ABSORBED),
				Map.entry(Statistic.DAMAGE_DEALT_RESISTED, VanillaStatistics.DAMAGE_DEALT_RESISTED),
				Map.entry(Statistic.DAMAGE_RESISTED, VanillaStatistics.DAMAGE_RESISTED),
				Map.entry(Statistic.DAMAGE_TAKEN, VanillaStatistics.DAMAGE_TAKEN),
				Map.entry(Statistic.DEATHS, VanillaStatistics.DEATHS),
				Map.entry(Statistic.DROP_COUNT, VanillaStatistics.DROP),
				Map.entry(Statistic.CAKE_SLICES_EATEN, VanillaStatistics.EAT_CAKE_SLICE),
				Map.entry(Statistic.ITEM_ENCHANTED, VanillaStatistics.ENCHANT_ITEM),
				Map.entry(Statistic.FALL_ONE_CM, VanillaStatistics.FALL_ONE_CM),
				Map.entry(Statistic.CAULDRON_FILLED, VanillaStatistics.FILL_CAULDRON),
				Map.entry(Statistic.FISH_CAUGHT, VanillaStatistics.FISH_CAUGHT),
				Map.entry(Statistic.FLY_ONE_CM, VanillaStatistics.FLY_ONE_CM),
				Map.entry(Statistic.HAPPY_GHAST_ONE_CM, VanillaStatistics.HAPPY_GHAST_ONE_CM),
				Map.entry(Statistic.HORSE_ONE_CM, VanillaStatistics.HORSE_ONE_CM),
				Map.entry(Statistic.DISPENSER_INSPECTED, VanillaStatistics.INSPECT_DISPENSER),
				Map.entry(Statistic.DROPPER_INSPECTED, VanillaStatistics.INSPECT_DROPPER),
				Map.entry(Statistic.HOPPER_INSPECTED, VanillaStatistics.INSPECT_HOPPER),
				Map.entry(Statistic.INTERACT_WITH_ANVIL, VanillaStatistics.INTERACT_WITH_ANVIL),
				Map.entry(Statistic.BEACON_INTERACTION, VanillaStatistics.INTERACT_WITH_BEACON),
				Map.entry(Statistic.INTERACT_WITH_BLAST_FURNACE, VanillaStatistics.INTERACT_WITH_BLAST_FURNACE),
				Map.entry(Statistic.BREWINGSTAND_INTERACTION, VanillaStatistics.INTERACT_WITH_BREWINGSTAND),
				Map.entry(Statistic.INTERACT_WITH_CAMPFIRE, VanillaStatistics.INTERACT_WITH_CAMPFIRE),
				Map.entry(Statistic.INTERACT_WITH_CARTOGRAPHY_TABLE, VanillaStatistics.INTERACT_WITH_CARTOGRAPHY_TABLE),
				Map.entry(Statistic.CRAFTING_TABLE_INTERACTION, VanillaStatistics.INTERACT_WITH_CRAFTING_TABLE),
				Map.entry(Statistic.FURNACE_INTERACTION, VanillaStatistics.INTERACT_WITH_FURNACE),
				Map.entry(Statistic.INTERACT_WITH_GRINDSTONE, VanillaStatistics.INTERACT_WITH_GRINDSTONE),
				Map.entry(Statistic.INTERACT_WITH_LECTERN, VanillaStatistics.INTERACT_WITH_LECTERN),
				Map.entry(Statistic.INTERACT_WITH_LOOM, VanillaStatistics.INTERACT_WITH_LOOM),
				Map.entry(Statistic.INTERACT_WITH_SMITHING_TABLE, VanillaStatistics.INTERACT_WITH_SMITHING_TABLE),
				Map.entry(Statistic.INTERACT_WITH_SMOKER, VanillaStatistics.INTERACT_WITH_SMOKER),
				Map.entry(Statistic.INTERACT_WITH_STONECUTTER, VanillaStatistics.INTERACT_WITH_STONECUTTER),
				Map.entry(Statistic.JUMP, VanillaStatistics.JUMP),
				Map.entry(Statistic.LEAVE_GAME, VanillaStatistics.LEAVE_GAME),
				Map.entry(Statistic.MINECART_ONE_CM, VanillaStatistics.MINECART_ONE_CM),
				Map.entry(Statistic.MOB_KILLS, VanillaStatistics.MOB_KILLS),
				Map.entry(Statistic.NAUTILUS_ONE_CM, VanillaStatistics.NAUTILUS_ONE_CM),
				Map.entry(Statistic.OPEN_BARREL, VanillaStatistics.OPEN_BARREL),
				Map.entry(Statistic.CHEST_OPENED, VanillaStatistics.OPEN_CHEST),
				Map.entry(Statistic.ENDERCHEST_OPENED, VanillaStatistics.OPEN_ENDERCHEST),
				Map.entry(Statistic.SHULKER_BOX_OPENED, VanillaStatistics.OPEN_SHULKER_BOX),
				Map.entry(Statistic.PIG_ONE_CM, VanillaStatistics.PIG_ONE_CM),
				Map.entry(Statistic.NOTEBLOCK_PLAYED, VanillaStatistics.PLAY_NOTEBLOCK),
				Map.entry(Statistic.RECORD_PLAYED, VanillaStatistics.PLAY_RECORD),
				Map.entry(Statistic.PLAY_ONE_MINUTE, VanillaStatistics.PLAY_TIME),
				Map.entry(Statistic.PLAYER_KILLS, VanillaStatistics.PLAYER_KILLS),
				Map.entry(Statistic.FLOWER_POTTED, VanillaStatistics.POT_FLOWER),
				Map.entry(Statistic.RAID_TRIGGER, VanillaStatistics.RAID_TRIGGER),
				Map.entry(Statistic.RAID_WIN, VanillaStatistics.RAID_WIN),
				Map.entry(Statistic.SLEEP_IN_BED, VanillaStatistics.SLEEP_IN_BED),
				Map.entry(Statistic.SNEAK_TIME, VanillaStatistics.SNEAK_TIME),
				Map.entry(Statistic.SPRINT_ONE_CM, VanillaStatistics.SPRINT_ONE_CM),
				Map.entry(Statistic.STRIDER_ONE_CM, VanillaStatistics.STRIDER_ONE_CM),
				Map.entry(Statistic.SWIM_ONE_CM, VanillaStatistics.SWIM_ONE_CM),
				Map.entry(Statistic.TALKED_TO_VILLAGER, VanillaStatistics.TALKED_TO_VILLAGER),
				Map.entry(Statistic.TARGET_HIT, VanillaStatistics.TARGET_HIT),
				Map.entry(Statistic.TIME_SINCE_DEATH, VanillaStatistics.TIME_SINCE_DEATH),
				Map.entry(Statistic.TIME_SINCE_REST, VanillaStatistics.TIME_SINCE_REST),
				Map.entry(Statistic.TOTAL_WORLD_TIME, VanillaStatistics.TOTAL_WORLD_TIME),
				Map.entry(Statistic.TRADED_WITH_VILLAGER, VanillaStatistics.TRADED_WITH_VILLAGER),
				Map.entry(Statistic.TRAPPED_CHEST_TRIGGERED, VanillaStatistics.TRIGGER_TRAPPED_CHEST),
				Map.entry(Statistic.NOTEBLOCK_TUNED, VanillaStatistics.TUNE_NOTEBLOCK),
				Map.entry(Statistic.CAULDRON_USED, VanillaStatistics.USE_CAULDRON),
				Map.entry(Statistic.WALK_ON_WATER_ONE_CM, VanillaStatistics.WALK_ON_WATER_ONE_CM),
				Map.entry(Statistic.WALK_ONE_CM, VanillaStatistics.WALK_ONE_CM),
				Map.entry(Statistic.WALK_UNDER_WATER_ONE_CM, VanillaStatistics.WALK_UNDER_WATER_ONE_CM),
				Map.entry(Statistic.BREAK_ITEM, VanillaStatistics.BROKEN),
				Map.entry(Statistic.CRAFT_ITEM, VanillaStatistics.CRAFTED),
				Map.entry(Statistic.DROP, VanillaStatistics.DROPPED),
				Map.entry(Statistic.KILL_ENTITY, VanillaStatistics.KILLED),
				Map.entry(Statistic.ENTITY_KILLED_BY, VanillaStatistics.KILLED_BY),
				Map.entry(Statistic.MINE_BLOCK, VanillaStatistics.MINED),
				Map.entry(Statistic.PICKUP, VanillaStatistics.PICKED_UP),
				Map.entry(Statistic.USE_ITEM, VanillaStatistics.USED)
		));

		return map;
	}
}
