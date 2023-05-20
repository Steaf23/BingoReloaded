package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.util.Message;

public class BingoParticipantJoinEvent extends BingoEvent
{
    public final BingoParticipant participant;

    public BingoParticipantJoinEvent(BingoParticipant participant)
    {
        super(participant.getSession());
        this.participant = participant;
        Message.log("Player " + participant.getDisplayName() + " joined the game", session.worldName);
    }
}
