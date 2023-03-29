package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.util.Message;

public class BingoPlayerJoinEvent extends BingoEvent
{
    public final BingoPlayer player;

    public BingoPlayerJoinEvent(BingoPlayer player)
    {
        super(player.session);
        this.player = player;
        Message.log("Player " + player.displayName + " joined the game", session.worldName);
    }
}
