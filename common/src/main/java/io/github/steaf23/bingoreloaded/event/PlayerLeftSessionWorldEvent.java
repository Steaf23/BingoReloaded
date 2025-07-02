package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.event.core.BingoEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;

public class PlayerLeftSessionWorldEvent extends BingoEvent
{
    private final PlayerHandle player;

    public PlayerLeftSessionWorldEvent(PlayerHandle player, BingoSession session) {
        super(session);
        this.player = player;
    }

    public PlayerHandle getPlayer() {
        return player;
    }
}
