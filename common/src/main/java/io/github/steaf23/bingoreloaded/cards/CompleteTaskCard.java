package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemodes;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.tracker.TaskProgressTracker;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CompleteTaskCard extends TaskCard {

	private final int completeGoal;

	public CompleteTaskCard(@NotNull CardMenu menu, CardSize size, int completeGoal, TaskProgressTracker progressTracker) {
		super(menu, size, progressTracker);
		menu.setInfo(BingoMessage.INFO_COMPLETE_NAME.asPhrase(),
				BingoMessage.INFO_COMPLETE_DESC.asMultiline());
		this.completeGoal = completeGoal;
	}

	@Override
	public BingoGamemode getMode() {
		return BingoGamemodes.COMPLETE;
	}

	@Override
	public boolean hasTeamWon(@NotNull BingoTeam team) {
		return getCompleteCount(team) == Math.min(completeGoal, size.fullCardSize);
	}

	@Override
	public TaskCard copy(@Nullable Component alternateTitle) {
		CompleteTaskCard card = new CompleteTaskCard(menu.copy(alternateTitle), this.size, this.completeGoal, getProgressTracker());
		List<GameTask> newTasks = new ArrayList<>();
		for (var t : getTasks()) {
			newTasks.add(t.copy());
		}
		card.setTasks(newTasks);
		return card;
	}

	@Override
	public boolean canGenerateSeparateCards() {
		return true;
	}
}
