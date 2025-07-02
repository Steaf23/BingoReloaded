package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.event.core.BingoEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.GameTask;

public class BingoTaskProgressCompletedEvent extends BingoEvent
{
    private final GameTask task;

    public BingoTaskProgressCompletedEvent(BingoSession session, GameTask task) {
        super(session);
        this.task = task;
    }

    public GameTask getTask() {
        return task;
    }
}
