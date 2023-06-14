package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import org.bukkit.entity.Player;

public class PlayerLeftSessionWorldEvent extends BingoEvent
{
    private final Player player;

    public PlayerLeftSessionWorldEvent(Player player, BingoSession session)
    {
        super(session);
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }
}
