package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.cards.slot.TaskSlot;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemodes;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlitzTaskCard extends TaskCard {

	List<TaskSlot> wrappers = new ArrayList<>();

	public BlitzTaskCard(CardMenu menu, CardSize size, BingoGame game) {
		super(menu, size);

		game.getTimer().addNotifier(this::updateWithTime);
	}

	@Override
	public BingoGamemode getMode() {
		return BingoGamemodes.BLITZ;
	}

	@Override
	public boolean hasTeamWon(BingoTeam team) {
		return false;
	}

	// Cannot be copied because there is only 1 team.
	@Override
	public TaskCard copy(@Nullable Component alternateTitle) {
		return this;
	}

	@Override
	public boolean canGenerateSeparateCards() {
		return false;
	}

	private void updateWithTime(long newTime) {
		for (TaskSlot wrapper : wrappers) {

		}
	}
}
