package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.event.core.BingoEvent;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;

public class UpdateStatisticEvent extends BingoEvent
{
    public final BingoPlayer player;

    public UpdateStatisticEvent(BingoPlayer player)
    {
        super(player.getSession());
        this.player = player;
    }
}
