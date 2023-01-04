package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.util.CountdownTimer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CountdownTimerFinishedEvent extends Event
{
    private static final HandlerList HANDLERS = new HandlerList();

    public CountdownTimerFinishedEvent()
    {
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
