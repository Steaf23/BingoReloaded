package io.github.steaf23.bingoreloaded.protocol;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.network.packets.DataWriter;
import io.github.steaf23.bingoreloaded.data.helper.TaskFormatting;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.DataOutputStream;
import java.io.IOException;

public class TaskDefinitionProtocol {

	public static void writeTaskData(DataOutputStream stream, TaskData task) throws IOException {
		//id
		switch (task) {
			case ItemTask item -> {
				DataWriter.writeString(stream, "ITEM");
				DataWriter.writeString(stream, item.itemType().key().asString());
			}
			case AdvancementTask advancement -> {
				DataWriter.writeString(stream, "ADVANCEMENT");
				DataWriter.writeString(stream, advancement.advancement().key().asString());
			}
			case StatisticTask statistic -> {
				DataWriter.writeString(stream, "STATISTIC");
				DataWriter.writeString(stream, statistic.statistic().type().key().asString());
				EntityType entity = statistic.statistic().entityType();
				if (entity != null) {
					DataWriter.writeString(stream, entity.key().asString());
				}
				ItemType itemType = statistic.statistic().itemType();
				if (itemType != null) {
					DataWriter.writeString(stream, itemType.key().asString());
				}

				if (entity == null && itemType == null) {
					DataWriter.writeString(stream, "bingoreloaded:dummy");
				}
			}
			default -> {
			}
		}
		//name
		DataWriter.writeString(stream, PlainTextComponentSerializer.plainText().serialize(task.getName(TaskFormatting.DEFAULT)));
		//description
		DataWriter.writeString(stream, PlainTextComponentSerializer.plainText().serialize(task.getChatDescription(TaskFormatting.DEFAULT)));
		//iconItem
		DataWriter.writeString(stream, task.getDisplayMaterial(CardDisplayInfo.DUMMY_DISPLAY_INFO).key().asString());
		//category
		DataWriter.writeString(stream, "bingoreloaded:category/all");
		//maxCount
		stream.writeInt(switch (task.getType()) {
			case ITEM, STATISTIC -> 64;
			case ADVANCEMENT -> 1;
		});
	}
}
