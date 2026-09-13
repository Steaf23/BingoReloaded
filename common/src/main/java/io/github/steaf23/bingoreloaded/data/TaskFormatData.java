package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskFormatting;

public class TaskFormatData {
	public static final TaskFormatting DEFAULT = new TaskFormatting(
			"<yellow>x<1> <0></yellow>",
			"<green><italic>[<0>]</italic></green>",
			"<0>",
			"<light_purple><italic>*<0>: <1><2>*</italic></light_purple>",
			"<light_purple><italic>*<0> <bold><1></bold>: <2>*</italic></light_purple>",
			"<light_purple><italic>*<0> (<bold><1><bold>)*</italic></light_purple>",
			"<light_purple><italic>*(<bold><1><bold>) <0>*</italic></light_purple>"
	);

	public static TaskFormatting fromDataAccessor() {
		var data = BingoReloaded.getDataAccessor("taskformat");

		return new TaskFormatting(
				data.getString("types.item.name", DEFAULT.itemName()),
				data.getString("types.advancement.name", DEFAULT.itemName()),
				data.getString("types.advancement.description", DEFAULT.itemName()),
				data.getString("types.statistic.name", DEFAULT.itemName()),
				data.getString("types.statistic.name_item", DEFAULT.itemName()),
				data.getString("types.statistic.name_kill_entity", DEFAULT.itemName()),
				data.getString("types.statistic.name_killed_by_entity", DEFAULT.itemName())
		);
	}
}
