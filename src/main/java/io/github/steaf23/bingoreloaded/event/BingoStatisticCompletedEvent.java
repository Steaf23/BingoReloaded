package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.item.tasks.statistics.BingoStatistic;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;

public class BingoStatisticCompletedEvent extends BingoEvent
{
    public final BingoStatistic stat;
    public final BingoPlayer player;

    public BingoStatisticCompletedEvent(BingoStatistic stat, BingoPlayer player)
    {
        super(player.worldName());
        this.stat = stat;
        this.player = player;
    }
}
