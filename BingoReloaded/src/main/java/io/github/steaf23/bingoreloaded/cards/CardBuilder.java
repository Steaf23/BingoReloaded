package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;

public class CardBuilder
{
    public static BingoCard fromMode(MenuManager menuManager, BingoGamemode mode, CardSize size, int teamCount)
    {
        return switch (mode)
                {
                    case LOCKOUT -> new LockoutBingoCard(menuManager, size, teamCount);
                    case COMPLETE -> new CompleteBingoCard(menuManager, size);
                    default -> new BingoCard(menuManager, size);
                };
    }
}
