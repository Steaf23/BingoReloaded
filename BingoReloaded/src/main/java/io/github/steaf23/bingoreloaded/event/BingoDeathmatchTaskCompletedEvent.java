package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;

public class BingoDeathmatchTaskCompletedEvent extends BingoTaskProgressCompletedEvent
{
    public BingoDeathmatchTaskCompletedEvent(BingoSession session, BingoTask task) {
        super(session, task);
    }
}
