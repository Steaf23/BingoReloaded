package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.gui.base.MenuBoard;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;

public class HotswapBingoCard extends BingoCard
{
    private final int winningScore;

    public HotswapBingoCard(MenuBoard menuBoard, CardSize size) {
        this(menuBoard, size, -1);
    }

    public HotswapBingoCard(MenuBoard menuBoard, CardSize size, int winningScore) {
        super(menuBoard, size);
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
