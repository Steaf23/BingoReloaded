package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.BingoGame;
import org.bukkit.event.HandlerList;

public class CountdownTimerFinishedEvent extends BingoEvent
{
    private static final HandlerList HANDLERS = new HandlerList();

    public CountdownTimerFinishedEvent(BingoGame game)
    {
        super(game);
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
}
