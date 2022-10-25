package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.util.CountdownTimer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CountdownFinishedEvent extends Event
{
    private CountdownTimer owner;
    private static final HandlerList HANDLERS = new HandlerList();

    public CountdownFinishedEvent(CountdownTimer owner)
    {
        this.owner = owner;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public CountdownTimer getOwner()
    {
        return owner;
    }
}
