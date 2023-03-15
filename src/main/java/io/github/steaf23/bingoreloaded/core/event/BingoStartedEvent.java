package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.BingoGame;

/**
 * Event that will be fired right before the game starts.
 */
public class BingoStartedEvent extends BingoEvent
{
    public BingoStartedEvent(BingoGame game)
    {
        super(game);
    }
}
