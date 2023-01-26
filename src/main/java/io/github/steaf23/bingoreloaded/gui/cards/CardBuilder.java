package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.util.FlexColor;

public class CardBuilder
{
    public static BingoCard fromMode(BingoGamemode mode, CardSize size, int teamCount)
    {
        return switch (mode)
                {
                    case LOCKOUT -> new LockoutBingoCard(size, teamCount);
                    case COMPLETE -> new CompleteBingoCard(size);
                    case COUNTDOWN -> new CountdownBingoCard(size);
                    default -> new BingoCard(size);
                };
    }
}
