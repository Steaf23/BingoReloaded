package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.BingoGame;
import io.github.steaf23.bingoreloaded.core.player.BingoTeam;

import javax.annotation.Nullable;

/**
 * Event that will fire right before the game ends.
 */
public class BingoEndedEvent extends BingoEvent
{
    public final long totalGameTime;
    public final BingoTeam winningTeam;

    public BingoEndedEvent(long totalGameTime, @Nullable BingoTeam winningTeam, BingoGame game)
    {
        super(game);
        this.totalGameTime = totalGameTime;
        this.winningTeam = winningTeam;
    }
}
