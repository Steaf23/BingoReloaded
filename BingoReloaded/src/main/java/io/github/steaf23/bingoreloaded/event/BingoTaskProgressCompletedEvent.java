package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.event.core.BingoEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;

public class BingoTaskProgressCompletedEvent extends BingoEvent
{
    private final BingoTask task;

    public BingoTaskProgressCompletedEvent(BingoSession session, BingoTask task) {
        super(session);
        this.task = task;
    }

    public BingoTask getTask() {
        return task;
    }
}
