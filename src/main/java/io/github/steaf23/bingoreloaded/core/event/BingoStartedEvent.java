package io.github.steaf23.bingoreloaded.core.event;

/**
 * Event that will be fired right before the game starts.
 */
public class BingoStartedEvent extends BingoEvent
{
    public BingoStartedEvent(String worldName)
    {
        super(worldName);
    }
}
