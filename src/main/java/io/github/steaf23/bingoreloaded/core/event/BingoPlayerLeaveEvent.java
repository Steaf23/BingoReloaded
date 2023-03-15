package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.util.Message;

public class BingoPlayerLeaveEvent extends BingoEvent
{
    public final BingoPlayer player;

    public BingoPlayerLeaveEvent(BingoPlayer player)
    {
        super(player.game);
        this.player = player;
        Message.log("Player " + player.displayName + " left the game", game.getWorldName());
    }
}
