package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.BingoEventManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class BingoEvent extends Event
{
    private final String world;
    private static final HandlerList HANDLERS = new HandlerList();

    protected BingoEvent(String worldName)
    {
        this.world = worldName;
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

    public String getWorldName()
    {
        return world;
    }
}
