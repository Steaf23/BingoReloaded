package io.github.steaf23.bingoreloaded.lib;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypeHytale;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

public class HytaleTaskItemConverter {

	public static List<ItemStack> extractAsItems(TaskCard card) {
		return card.getTasks().stream()
				.map(HytaleTaskItemConverter::toItem)
				.toList();
	}

	public static ItemStack toItem(GameTask task) {
		ItemStack item = new ItemStack(((ItemTypeHytale)task.icon(CardDisplayInfo.DUMMY_DISPLAY_INFO)).itemId());
		// Step 1: create the item and put the new name, description and material on it.
		if (task.isVoided()) // VOIDED TASK
		{
			Component[] addedDesc = BingoMessage.VOIDED.asMultiline(NamedTextColor.DARK_GRAY);

//			item.setName(task.getName());
//			item.setLore(addedDesc);
//			item.setGlowing(true);
		}
		else if (task.isCompleted()) // COMPLETED TASK
		{
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

			item = new ItemStack("Go_Up_Wand");
		}
		else // DEFAULT TASK
		{
			item = item.withQuantity(task.data.getRequiredAmount());
		}

		// STEP 2: Add additional stuff like pdc data and glowing effect.

//		TagDataStorage storage = new TagDataStorage();
//		new GameTaskSerializer().toDataStorage(storage, task);
//		item.setExtraData(storage);
//
//		if ((task.data.shouldItemGlow() || task.isCompleted()) && !task.isVoided())
//		{
//			item.setGlowing(true);
//		}
//
//		item.setMaxStackSize(64);
		return item;
	}
}
