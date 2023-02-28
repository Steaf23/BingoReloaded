package io.github.steaf23.bingoreloaded.core.cards;

import io.github.steaf23.bingoreloaded.core.BingoGamemode;

public class CardBuilder
{
    public static BingoCard fromMode(BingoGamemode mode, CardSize size, int teamCount)
    {
        return switch (mode)
                {
                    case LOCKOUT -> new LockoutBingoCard(size, teamCount);
                    case COMPLETE -> new CompleteBingoCard(size);
                    default -> new BingoCard(size);
                };
    }
}
