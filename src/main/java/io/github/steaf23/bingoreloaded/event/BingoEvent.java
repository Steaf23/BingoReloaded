package io.github.steaf23.bingoreloaded.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class BingoEvent extends Event
{
    public final String worldName;
    private static final HandlerList HANDLERS = new HandlerList();

    protected BingoEvent(String worldName)
    {
        this.worldName = worldName;
    }

    @NotNull
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
