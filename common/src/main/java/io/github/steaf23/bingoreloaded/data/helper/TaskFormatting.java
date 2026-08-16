package io.github.steaf23.bingoreloaded.data.helper;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import net.kyori.adventure.text.Component;

public record TaskFormatting(String itemName,
							 String advancementName,
							 String advancementDescription,
							 String statisticName,
							 String statisticItem,
							 String statisticKillEntity,
							 String statisticKilledByEntity) {

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

	public Component itemNameComponent(ItemTask task) {
		return BingoMessage.createPhrase(itemName, false,
				ComponentUtils.itemName(task.itemType()),
				Component.text(task.count()));
	}

	public Component advancementNameComponent(AdvancementTask task) {
		return BingoMessage.createPhrase(advancementName, false,
				task.advancement().displayName());
	}

	public Component advancementDescriptionComponent(AdvancementTask task) {
		return BingoMessage.createPhrase(advancementDescription, false,
				task.advancement().description());
	}

	public Component statisticNameComponent(StatisticTask task, Component units, int countMultiplier) {
		return BingoMessage.createPhrase(statisticName, false,
				ComponentUtils.statistic(task.statistic()),
				Component.text(task.count() * countMultiplier),
				units);
	}

	public Component statisticItemComponent(StatisticTask task) {
		return BingoMessage.createPhrase(statisticItem, false,
				ComponentUtils.statistic(task.statistic()),
				ComponentUtils.itemName(task.statistic().itemType()),
				Component.text(task.count()));
	}

	public Component statisticKillEntityComponent(StatisticTask task) {
		Component[] inPlaceArgs = new Component[]{Component.text(task.count()), Component.empty()};
		return BingoMessage.createPhrase(statisticKillEntity, false,
				ComponentUtils.statistic(task.statistic(), inPlaceArgs),
				ComponentUtils.entityName(task.statistic().entityType()));
	}

	public Component statisticKilledByEntityComponent(StatisticTask task) {
		Component[] inPlaceArgs = new Component[]{Component.empty(), Component.text(task.count()), Component.empty()};
		return BingoMessage.createPhrase(statisticKilledByEntity, false,
				ComponentUtils.statistic(task.statistic(), inPlaceArgs),
				ComponentUtils.entityName(task.statistic().entityType()));
	}

}
