package io.github.steaf23.bingoreloaded.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SendBingoGameEvent extends Event
{
    private static final HandlerList HANDLERS = new HandlerList();

    public final BingoGameEvent eventType;

    public SendBingoGameEvent(BingoGameEvent eventType)
    {
        this.eventType = eventType;
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
