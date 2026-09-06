package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.platform.GameContext;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.statistics.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistic;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistics;
import io.github.steaf23.bingoreloaded.protocol.data.CreatorTaskSupplier;
import io.github.steaf23.bingoreloaded.protocol.data.task.AdvancementNode;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticCategory;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticNode;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CreatorTaskFactory {

	public static Map<Key, AdvancementNode> allAdvancements(PlatformServer server) {
		Map<Key, AdvancementNode> tasks = new HashMap<>();
		for (AdvancementHandle advancement : server.allAdvancements()) {
			String key = advancement.key().value();
			if (key.startsWith("recipes/")) {
				continue;
			}

			Key icon = null;
			String name = null;
			String desc = null;

			if (advancement.hasDisplay()) {
				icon = advancement.displayIcon().key();
				name = PlainTextComponentSerializer.plainText().serialize(advancement.displayName());
				desc = PlainTextComponentSerializer.plainText().serialize(advancement.description());
			}

			AdvancementNode node = new AdvancementNode(
					Optional.ofNullable(advancement.getParent()).map(AdvancementHandle::getParent).map(AdvancementHandle::key),
					advancement.hasDisplay(),
					icon,
					name,
					desc
			);
			tasks.put(advancement.key(), node);
		}

		return tasks;
	}

	public static List<TaskData> allAdvancementTasks(PlatformServer server) {
		List<TaskData> tasks = new ArrayList<>();
		for (AdvancementHandle advancement : server.allAdvancements()) {
			String key = advancement.key().value();
			if (key.startsWith("recipes/") || key.endsWith("/root")) {
				continue;
			}

			if (!advancement.hasDisplay()) {
				continue;
			}

			AdvancementTask task = new AdvancementTask(advancement);
			tasks.add(task);
		}

		return tasks;
	}

	public static List<TaskData> allItems() {
		List<TaskData> tasks = new ArrayList<>();
		for (ItemType m : PlatformResolver.getRegistries().allItems()) {
			if (!m.isAir()) {
				tasks.add(new ItemTask(m, 1));
			}
		}

		return tasks;
	}

	public static Set<Key> uniqueItems() {
		Set<Key> tasks = new HashSet<>();
		for (ItemType m : PlatformResolver.getRegistries().allItems()) {
			if (!m.isAir()) {
				tasks.add(m.key());
			}
		}

		return tasks;
	}

	public static Set<Key> uniqueBlocks() {
		Set<Key> tasks = new HashSet<>();
		for (ItemType m : PlatformResolver.getRegistries().allItems()) {
			if (!m.isAir() && m.isBlock()) {
				tasks.add(m.key());
			}
		}

		return tasks;
	}

	public static Set<Key> uniqueEntities(BingoReloadedRuntime runtime) {
		Set<Key> tasks = new HashSet<>();
		for (EntityType entityType : runtime.getValidEntityTypesForStatistics()) {
				tasks.add(entityType.key());
		}

		return tasks;
	}

	public static List<StatisticNode> allStatisticTypes() {
		List<StatisticNode> tasks = new ArrayList<>();
		Map<StatisticCategory, List<VanillaStatistic>> stats = VanillaStatistics.byCategory();
		for (StatisticCategory cat : stats.keySet()) {
			tasks.addAll(stats.get(cat).stream()
					.filter(t ->
							t != VanillaStatistics.TIME_SINCE_DEATH &&
							t != VanillaStatistics.TIME_SINCE_REST &&
							t != VanillaStatistics.TOTAL_WORLD_TIME &&
							t != VanillaStatistics.LEAVE_GAME)
					.map(stat -> {
						StatisticHandle defaultHandle = new StatisticHandle(stat, EntityType.of(Key.key("zombie")), ItemType.of("dirt"));
						return new StatisticNode(
								new TaskId.Statistic(
										stat.key(),
										stat.specification(defaultHandle),
										cat),
								stat.key().asString(),
								stat.category().type == StatisticCategory.Type.CUSTOM,
								stat.iconFunction().apply(defaultHandle).key());
					})
					.toList());
		}

		return tasks;
	}

	public static Map<Key, Key> allCustomStatistics() {
		Map<Key, Key> customStatistics = new HashMap<>();
		Map<StatisticCategory, List<VanillaStatistic>> stats = VanillaStatistics.byCategory();
		for (StatisticCategory cat : stats.keySet()) {
			if (cat.type != StatisticCategory.Type.CUSTOM) {
				continue;
			}

			for (VanillaStatistic stat : stats.get(cat)) {
				customStatistics.put(stat.key(), stat.iconFunction().apply(new StatisticHandle(stat)).key());
			}
		}

		return customStatistics;
	}

	public static CreatorTaskSupplier create(GameContext context) {
		return new CreatorTaskSupplier(
				uniqueItems(),
				uniqueBlocks(),
				uniqueEntities(context.runtime()),
				allAdvancements(context.server()),
				allStatisticTypes());
	}
}
