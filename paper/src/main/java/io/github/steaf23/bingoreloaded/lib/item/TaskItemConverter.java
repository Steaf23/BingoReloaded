package io.github.steaf23.bingoreloaded.lib.item;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.cards.hotswap.ExpiringHotswapTask;
import io.github.steaf23.bingoreloaded.cards.hotswap.HotswapTaskHolder;
import io.github.steaf23.bingoreloaded.cards.hotswap.SimpleHotswapTask;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.GameTaskSerializer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class TaskItemConverter {

	public static ItemTemplate taskToItem(GameTask task, CardDisplayInfo displayInfo)
	{
		ItemTemplate item;
		// Step 1: create the item and put the new name, description and material on it.
		if (task.isVoided()) // VOIDED TASK
		{
			item = new ItemTemplate(ItemType.of("structure_void"), null);
			Component[] addedDesc = BingoMessage.VOIDED.asMultiline(NamedTextColor.DARK_GRAY);

			item.setName(task.getName());
			item.setLore(addedDesc);
			item.setGlowing(true);
		}
		else if (task.isCompleted()) // COMPLETED TASK
		{
			ItemType completeMaterial = ItemType.of("barrier");

			String timeString = GameTimer.getTimeAsString(task.completedAt);

			BingoParticipant completedBy = task.getCompletedByPlayer().orElseThrow();

			Component[] desc = BingoMessage.COMPLETED_LORE.asMultiline(NamedTextColor.DARK_PURPLE,
					completedBy.getDisplayName()
							.color(completedBy.getTeam().getColor())
							.decorate(TextDecoration.BOLD)
							.decorate(TextDecoration.ITALIC),
					Component.text(timeString)
							.color(NamedTextColor.GOLD)
							.decorate(TextDecoration.ITALIC));

			item = new ItemTemplate(completeMaterial, task.getName(), desc);
		}
		else // DEFAULT TASK
		{
			item = new ItemTemplate(task.icon(displayInfo), task.data.getName(), task.data.getItemDescription());
			item.setAmount(task.data.getRequiredAmount());
		}

		// STEP 2: Add additional stuff like pdc data and glowing effect.

		TagDataStorage storage = new TagDataStorage();
		new GameTaskSerializer().toDataStorage(storage, task);
		item.setExtraData(storage);

		if ((task.data.shouldItemGlow() || task.isCompleted()) && !task.isVoided())
		{
			item.setGlowing(true);
		}

		item.setMaxStackSize(64);
		return item;
	}

	public static ItemTemplate hotswapTaskToItem(HotswapTaskHolder task, CardDisplayInfo displayInfo) {
		return switch (task) {
			case ExpiringHotswapTask expiringTask -> expiringHotswapTaskToItem(expiringTask, displayInfo);
			case SimpleHotswapTask simpleTask -> simpleHotswapTaskToItem(simpleTask, displayInfo);
			default -> throw new IllegalStateException("Unexpected value: " + task);
		};
	}

	private static ItemTemplate expiringHotswapTaskToItem(ExpiringHotswapTask task, CardDisplayInfo displayInfo) {
		ItemTemplate item = taskToItem(task.getTask(), displayInfo);
		if (task.isRecovering()) {
			item.addDescription("time", 1, BingoMessage.HOTSWAP_RECOVER.asPhrase(GameTimer.getTimeAsComponent(task.getCurrentTime())).color(TextColor.fromHexString("#5cb1ff")));
		}
		else {
			item.addDescription("time", 1, BingoMessage.HOTSWAP_EXPIRE.asPhrase(GameTimer.getTimeAsComponent(task.getCurrentTime())).color(task.getColorForExpirationTime()));
			if (task.showExpirationAsDurability) {
				item.setMaxDamage(task.expirationTimeSeconds);
				item.setDamage(task.getCurrentTime());
			}
		}
		return item;
	}

	private static ItemTemplate simpleHotswapTaskToItem(SimpleHotswapTask task, CardDisplayInfo displayInfo) {
		ItemTemplate item = taskToItem(task.getTask(), displayInfo);
		if (task.isRecovering()) {
			item.addDescription("time", 1, BingoMessage.HOTSWAP_RECOVER.asPhrase(GameTimer.getTimeAsComponent(task.getCurrentTime())).color(TextColor.fromHexString("#5cb1ff")));
		}
		return item;
	}
}
