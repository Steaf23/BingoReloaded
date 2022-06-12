package me.steven.bingoreloaded.criteria;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class BingoCriteriaCompleteEvent extends PlayerEvent
{
    private static final HandlerList HANDLERS = new HandlerList();
    private final IBingoCriteria criteria;

    public BingoCriteriaCompleteEvent(IBingoCriteria criteria, Player player)
    {
        super(player);
        this.criteria = criteria;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public IBingoCriteria getCriteria()
    {
        return criteria;
    }
}
