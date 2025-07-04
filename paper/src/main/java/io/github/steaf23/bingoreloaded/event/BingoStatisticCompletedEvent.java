package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.event.core.BingoEvent;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandlePaper;

public class BingoStatisticCompletedEvent extends BingoEvent
{
    public final StatisticHandlePaper stat;
    public final BingoParticipant player;

    public BingoStatisticCompletedEvent(StatisticHandlePaper stat, BingoParticipant player)
    {
        super(player.getSession());
        this.stat = stat;
        this.player = player;
    }

    public BingoParticipant getParticipant() {
        return player;
    }

    public StatisticHandlePaper getStatistic() {
        return stat;
    }
}
