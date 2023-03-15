package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;

public class UpdateStatisticEvent extends BingoEvent
{
    public final BingoPlayer player;

    public UpdateStatisticEvent(BingoPlayer player)
    {
        super(player.game);
        this.player = player;
    }
}
