package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.GameTask;

public class BingoDeathmatchTaskCompletedEvent extends BingoTaskProgressCompletedEvent
{
    public BingoDeathmatchTaskCompletedEvent(BingoSession session, GameTask task) {
        super(session, task);
    }
}
