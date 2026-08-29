package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;

import java.util.ArrayList;
import java.util.List;

public class Tasks {

	public static List<TaskData> allAdvancements(PlatformServer server) {
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

	public static List<TaskData> allTasks(PlatformServer server) {
		List<TaskData> all = new ArrayList<>();
		all.addAll(allItems());
		all.addAll(allAdvancements(server));
		return all;
	}
}
