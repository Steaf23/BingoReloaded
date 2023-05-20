package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.util.Message;

public class BingoParticipantLeaveEvent extends BingoEvent
{
    public final BingoParticipant participant;

    public BingoParticipantLeaveEvent(BingoParticipant participant)
    {
        super(participant.getSession());
        this.participant = participant;
        Message.log("Player " + participant.getDisplayName() + " left the game", session.worldName);
    }
}
