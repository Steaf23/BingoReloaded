package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;

public class ParticipantCountChangedEvent extends BingoEvent
{
    public final int oldAmount;
    public final int newAmount;

    public ParticipantCountChangedEvent(BingoSession session, int oldAmount, int newAmount) {
        super(session);
        this.oldAmount = oldAmount;
        this.newAmount = newAmount;
    }
}
