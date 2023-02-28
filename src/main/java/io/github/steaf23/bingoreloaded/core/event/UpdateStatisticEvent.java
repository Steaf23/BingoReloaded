package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;

public class UpdateStatisticEvent extends BingoEvent
{
    public final BingoPlayer player;

    public UpdateStatisticEvent(BingoPlayer player, String worldName)
    {
        super(worldName);
        this.player = player;
    }
}
