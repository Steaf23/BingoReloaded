package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.easymenulib.menu.MenuBoard;

public class HotswapBingoCard extends BingoCard
{
    private final int winningScore;
    private final GameTimer taskTimer;

    public HotswapBingoCard(MenuBoard menuBoard, CardSize size) {
        this(menuBoard, size, -1);
    }

    public HotswapBingoCard(MenuBoard menuBoard, CardSize size, int winningScore) {
        super(menuBoard, size);
        this.winningScore = winningScore;
        this.taskTimer = new CounterTimer();
        taskTimer.start();
        menu.setInfo("HotSwap",
                "Items will get replaced when they expire after some amount of time. ",
                "First team to complete " + winningScore + " tasks wins!");
    }

    @Override
    public boolean hasBingo(BingoTeam team) {
        if (winningScore == -1)
        {
            return false;
        }
        return getCompleteCount(team) == winningScore;
    }

    // Lockout cards cannot be copied since it should be the same instance for every player.
    @Override
    public HotswapBingoCard copy() {
        return this;
    }
}
