package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;

public class BingoStatisticCompletedEvent extends BingoEvent
{
    public final BingoStatistic stat;
    public final BingoParticipant player;

    public BingoStatisticCompletedEvent(BingoStatistic stat, BingoParticipant player)
    {
        super(player.getSession());
        this.stat = stat;
        this.player = player;
    }
}
