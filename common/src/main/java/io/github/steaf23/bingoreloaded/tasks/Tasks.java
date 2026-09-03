package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.platform.GameContext;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.statistics.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistic;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistics;
import io.github.steaf23.bingoreloaded.protocol.data.CreatorTaskSupplier;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticCategory;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tasks {

	public static List<TaskDefinition> allAdvancements(PlatformServer server) {
		List<AdvancementTask> tasks = new ArrayList<>();
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

		return tasks.stream()
				.map(t -> new TaskDefinition(
						new TaskId.Advancement(t.advancement().key()),
						PlainTextComponentSerializer.plainText().serialize(t.advancement().displayName()),
						PlainTextComponentSerializer.plainText().serialize(t.advancement().description()),
						t.getDisplayMaterial(CardDisplayInfo.DUMMY_DISPLAY_INFO).key(),
						"",
						1))
						.toList();
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

	public static List<Key> uniqueItems() {
		List<Key> tasks = new ArrayList<>();
		for (ItemType m : PlatformResolver.getRegistries().allItems()) {
			if (!m.isAir()) {
				tasks.add(m.key());
			}
		}

		return tasks;
	}

	public static List<Key> uniqueBlocks() {
		List<Key> tasks = new ArrayList<>();
		for (ItemType m : PlatformResolver.getRegistries().allItems()) {
			if (!m.isAir() && m.isBlock()) {
				tasks.add(m.key());
			}
		}

		return tasks;
	}

	public static List<Key> uniqueEntities(BingoReloadedRuntime runtime) {
		List<Key> tasks = new ArrayList<>();
		for (EntityType entityType : runtime.getValidEntityTypesForStatistics()) {
				tasks.add(entityType.key());
		}

		return tasks;
	}

	public static List<TaskDefinition> allStatisticTypes(BingoReloadedRuntime runtime) {
		List<TaskDefinition> tasks = new ArrayList<>();
		Map<StatisticCategory, List<VanillaStatistic>> stats = VanillaStatistics.byCategory();
		for (StatisticCategory cat : stats.keySet()) {
			tasks.addAll(stats.get(cat).stream()
					.map(stat -> {
						StatisticHandle defaultHandle = new StatisticHandle(stat, EntityType.of(Key.key("zombie")), ItemType.of("dirt"));
						return new TaskDefinition(
								new TaskId.Statistic(
										stat.key(),
										stat.specification(defaultHandle),
										cat),
								stat.keyStr(),
								"descripted",
								stat.iconFunction().apply(defaultHandle).key(),
								cat.name(),
								64);
					})
					.toList());
		}

		return tasks;
	}

	public static CreatorTaskSupplier allTasks(GameContext context) {
		return new CreatorTaskSupplier(allAdvancements(context.server()), allStatisticTypes(context.runtime()), uniqueItems(), uniqueBlocks(), uniqueEntities(context.runtime()));
	}
}
