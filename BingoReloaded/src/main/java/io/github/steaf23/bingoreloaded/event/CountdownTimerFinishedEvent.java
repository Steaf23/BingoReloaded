package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import org.bukkit.event.HandlerList;

public class CountdownTimerFinishedEvent extends BingoEvent
{
    private static final HandlerList HANDLERS = new HandlerList();

    private final CountdownTimer timer;

    public CountdownTimerFinishedEvent(BingoSession session, CountdownTimer timer)
    {
        super(session);
        this.timer = timer;
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

    public CountdownTimer getTimer()
    {
        return timer;
    }
}
