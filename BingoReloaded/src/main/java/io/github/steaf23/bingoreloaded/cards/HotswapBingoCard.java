package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;

public class HotswapBingoCard extends BingoCard
{
    private final int winningScore;

    public HotswapBingoCard(MenuManager menuManager, CardSize size) {
        this(menuManager, size, -1);
    }

    public HotswapBingoCard(MenuManager menuManager, CardSize size, int winningScore) {
        super(menuManager, size);
        this.winningScore = winningScore;
        menu.setInfo("HotSwap", "Items will randomly get replaced when they expire. Gather the most points to win!");
    }

    @Override
    public boolean hasBingo(BingoTeam team) {
        if (winningScore == -1)
        {
            return false;
        }
        return getCompleteCount(team) == winningScore;
    }
}
