package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.event.core.BingoEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;

/**
 * Event that will fire right before the game ends.
 */
public class BingoEndedEvent extends BingoEvent
{
    public final Events.BingoEnded bingoEnded;

    public BingoEndedEvent(BingoSession session, Events.BingoEnded bingoEnded)
    {
        super(session);
		this.bingoEnded = bingoEnded;
    }
}
