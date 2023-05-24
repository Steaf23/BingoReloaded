package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.game.BingoSession;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BingoSessionDestroyedEvent extends Event
{
    public final BingoSession session;

    private static final HandlerList HANDLERS = new HandlerList();

    public BingoSessionDestroyedEvent(BingoSession session)
    {
        this.session = session;
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
