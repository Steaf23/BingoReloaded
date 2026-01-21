package io.github.steaf23.bingoreloaded.ui.card;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TextCardMenu implements CardMenu {

	private List<GameTask> tasks = new ArrayList<>();
	private final CardDisplayInfo displayInfo;
	private Component title;

	public TextCardMenu(CardDisplayInfo displayInfo) {
		this.displayInfo = displayInfo;
	}

	@Override
	public void setInfo(Component title, Component... description) {
		this.title = title;
	}

	@Override
	public void updateTasks(List<GameTask> tasks) {
		this.tasks = tasks;
	}

	@Override
	public void open(PlayerHandle entity) {
		if (tasks.size() != displayInfo.size().fullCardSize) {
			entity.sendMessage(Component.text("No tasks have been selected yet!"));
			return;
		}

		entity.sendMessage(title);
		for (int y = 0; y < displayInfo.size().size; y++) {
			Component message = Component.empty();
			for (int x = 0; x < displayInfo.size().size; x++) {
				GameTask task = tasks.get(y * displayInfo.size().size + x);
				message = message.append(task.getName()).append(Component.text(" "));
			}
			entity.sendMessage(message);
		}
	}

	@Override
	public CardMenu copy(@Nullable Component alternateTitle) {
		return new TextCardMenu(displayInfo);
	}

	@Override
	public CardDisplayInfo displayInfo() {
		return displayInfo;
	}
}
