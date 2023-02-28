package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;

public class BingoPlayerJoinEvent extends BingoEvent
{
    public final BingoPlayer player;

    public BingoPlayerJoinEvent(BingoPlayer player, String worldName)
    {
        super(worldName);
        this.player = player;
    }
}
