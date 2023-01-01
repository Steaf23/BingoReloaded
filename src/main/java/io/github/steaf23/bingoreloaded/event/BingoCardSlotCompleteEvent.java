package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.item.tasks.AbstractBingoTask;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class BingoCardSlotCompleteEvent extends PlayerEvent
{
    private static final HandlerList HANDLERS = new HandlerList();
    private final AbstractBingoTask slot;
    private final BingoTeam team;
    private final boolean bingo;

    public BingoCardSlotCompleteEvent(AbstractBingoTask slot, BingoTeam team, Player player, boolean bingo)
    {
        super(player);
        this.slot = slot;
        this.team = team;
        this.bingo = bingo;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
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
}
