package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.event.core.BingoEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;

public class CountdownTimerFinishedEvent extends BingoEvent
{
    private final CountdownTimer timer;

    public CountdownTimerFinishedEvent(BingoSession session, CountdownTimer timer)
    {
        super(session);
        this.timer = timer;
    }

    public CountdownTimer getTimer()
    {
        return timer;
    }
}
