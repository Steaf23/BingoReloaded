package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.BingoSession;

/**
 * Event that will be fired right before the game starts.
 */
public class BingoStartedEvent extends BingoEvent
{
    public BingoStartedEvent(BingoSession session)
    {
        super(session);
    }
}
