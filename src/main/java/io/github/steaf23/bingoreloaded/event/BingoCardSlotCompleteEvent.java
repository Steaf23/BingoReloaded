package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.BingoEventManager;
import io.github.steaf23.bingoreloaded.item.tasks.AbstractBingoTask;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerEvent;

public class BingoCardSlotCompleteEvent extends BingoEvent
{
    private final AbstractBingoTask slot;
    private final BingoTeam team;
    private final boolean bingo;
    private final Player player;

    public BingoCardSlotCompleteEvent(AbstractBingoTask slot, BingoTeam team, Player player, boolean bingo, String worldName)
    {
        super(worldName);
        this.player = player;
        this.slot = slot;
        this.team = team;
        this.bingo = bingo;
    }

    public AbstractBingoTask getCardSlot()
    {
        return slot;
    }

    public boolean hasBingo()
    {
        return bingo;
    }

    public BingoTeam getTeam()
    {
        return team;
    }

    public Player getPlayer()
    {
        return player;
    }
}
