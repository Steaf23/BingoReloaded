package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.player.BingoTeam;

import javax.annotation.Nullable;

/**
 * Event that will fire right before the game ends.
 */
public class BingoEndedEvent extends BingoEvent
{
    public final long totalGameTime;
    public final BingoTeam winningTeam;

    public BingoEndedEvent(long totalGameTime, @Nullable BingoTeam winningTeam, String worldName)
    {
        super(worldName);
        this.totalGameTime = totalGameTime;
        this.winningTeam = winningTeam;
    }
}
