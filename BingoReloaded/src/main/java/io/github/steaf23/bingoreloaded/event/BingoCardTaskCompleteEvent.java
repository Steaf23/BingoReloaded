package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;

public class BingoCardTaskCompleteEvent extends BingoEvent
{
    private final BingoTask task;
    private final boolean bingo;
    private final BingoParticipant participant;

    public BingoCardTaskCompleteEvent(BingoTask task, BingoParticipant participant, boolean bingo)
    {
        super(participant.getSession());
        this.participant = participant;
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

    public BingoParticipant getParticipant()
    {
        return participant;
    }
}
