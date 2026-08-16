package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.helper.TaskFormatting;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemodes;

public record CardDisplayInfo(BingoGamemode mode,
                              CardSize size,
                              TaskDisplayMode advancementDisplay,
                              TaskDisplayMode statisticDisplay,
                              boolean allowViewingOtherCards,
                              TaskFormatting formatting) {

	public static final CardDisplayInfo DUMMY_DISPLAY_INFO = defaultWithFormatting(TaskFormatting.DEFAULT);

	public static CardDisplayInfo defaultWithFormatting(TaskFormatting formatting) {
		return new CardDisplayInfo(
				BingoGamemodes.BINGO,
				CardSize.X5,
				TaskDisplayMode.UNIQUE_TASK_ITEMS,
				TaskDisplayMode.UNIQUE_TASK_ITEMS,
				false,
				formatting
		);
	}
}
