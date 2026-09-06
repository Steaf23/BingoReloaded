package io.github.steaf23.bingoreloaded.protocol;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.data.helper.TaskFormatting;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.IOException;

public class TaskDefinitionProtocol {

	public static TaskDefinition taskDefinition(TaskData task) throws IOException {
		//id
		TaskId id = switch (task) {
			case ItemTask item -> new TaskId.Item(item.itemType().key());
			case AdvancementTask advancement ->
					new TaskId.Advancement(advancement.advancement().key());
			case StatisticTask statistic -> {
				Key type = statistic.statistic().type().nameOrGroup();
				Key key = null;
				EntityType entity = statistic.statistic().entityType();
				if (entity != null) {
					key = entity.key();
				}
				ItemType itemType = statistic.statistic().itemType();
				if (itemType != null) {
					key = itemType.key();
				}

				if (key == null) {
					key = type;
					type = Key.key("minecraft:custom");
				}

				yield new TaskId.Statistic(type, key, statistic.statistic().type().category());
			}
			default -> throw new IllegalStateException("Unexpected value: " + task);
		};

		//name
		String name = PlainTextComponentSerializer.plainText().serialize(task.getName(TaskFormatting.DEFAULT));
		//description
		String description = PlainTextComponentSerializer.plainText().serialize(task.getChatDescription(TaskFormatting.DEFAULT));
		//iconItem
		Key iconItem = task.getDisplayMaterial(CardDisplayInfo.DUMMY_DISPLAY_INFO).key();
		//category
		String category;
		if (task instanceof StatisticTask statisticTask) {
			category = statisticTask.statistic().type().category().name();
		} else {
			category = "bingoreloaded:category/all";
		}
		//maxCount
		int max = switch (task.getType()) {
			case ITEM, STATISTIC -> 64;
			case ADVANCEMENT -> 1;
		};

		return new TaskDefinition(id, name, description, iconItem, category, max);
	}
}
