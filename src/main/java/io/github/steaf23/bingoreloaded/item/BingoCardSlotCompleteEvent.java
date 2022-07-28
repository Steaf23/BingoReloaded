package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class BingoCardSlotCompleteEvent extends PlayerEvent
{
    private static final HandlerList HANDLERS = new HandlerList();
    private final AbstractCardSlot slot;
    private final BingoTeam team;
    private final boolean bingo;

    public BingoCardSlotCompleteEvent(AbstractCardSlot slot, BingoTeam team, Player player, boolean bingo)
    {
        super(player);
        this.slot = slot;
        this.team = team;
        this.bingo = bingo;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public AbstractCardSlot getCardSlot()
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
