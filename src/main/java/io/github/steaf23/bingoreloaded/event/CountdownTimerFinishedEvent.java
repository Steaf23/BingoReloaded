package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.util.CountdownTimer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CountdownTimerFinishedEvent extends BingoEvent
{
    private static final HandlerList HANDLERS = new HandlerList();

    public CountdownTimerFinishedEvent(String worldName)
    {
        super(worldName);
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
