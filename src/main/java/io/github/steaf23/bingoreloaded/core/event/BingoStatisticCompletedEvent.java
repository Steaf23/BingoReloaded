package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.core.tasks.statistics.BingoStatistic;

public class BingoStatisticCompletedEvent extends BingoEvent
{
    public final BingoStatistic stat;
    public final BingoPlayer player;

    public BingoStatisticCompletedEvent(BingoStatistic stat, BingoPlayer player)
    {
        super(player.session);
        this.stat = stat;
        this.player = player;
    }
}
