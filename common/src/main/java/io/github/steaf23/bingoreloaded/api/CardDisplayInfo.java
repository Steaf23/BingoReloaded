package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;

public record CardDisplayInfo(BingoGamemode mode,
							  CardSize size,
							  TaskDisplayMode advancementDisplay,
							  TaskDisplayMode statisticDisplay,
							  boolean allowViewingOtherCards) {

	public static final CardDisplayInfo DUMMY_DISPLAY_INFO = new CardDisplayInfo(
			BingoGamemode.REGULAR,
			CardSize.X5,
			TaskDisplayMode.UNIQUE_TASK_ITEMS,
			TaskDisplayMode.UNIQUE_TASK_ITEMS,
			false);
}
