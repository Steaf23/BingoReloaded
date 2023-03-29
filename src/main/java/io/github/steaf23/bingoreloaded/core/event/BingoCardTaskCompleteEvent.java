package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;

public class BingoCardTaskCompleteEvent extends BingoEvent
{
    private final BingoTask task;
    private final boolean bingo;
    private final BingoPlayer player;

    public BingoCardTaskCompleteEvent(BingoTask task, BingoPlayer player, boolean bingo)
    {
        super(player.session);
        this.player = player;
        this.task = task;
        this.bingo = bingo;
    }

    public BingoTask getTask()
    {
        return task;
    }

    public boolean hasBingo()
    {
        return bingo;
    }

    public BingoPlayer getPlayer()
    {
        return player;
    }
}
