package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PlayerJoinedSessionWorldEvent extends BingoEvent
{
    private final Player player;

    public PlayerJoinedSessionWorldEvent(Player player, BingoSession session) {
        super(session);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
