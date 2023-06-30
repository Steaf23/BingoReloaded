package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoTeam;

public class ParticipantLeftTeamEvent extends BingoEvent
{
    private final BingoParticipant participant;
    private final BingoTeam team;

    public ParticipantLeftTeamEvent(BingoParticipant participant, BingoTeam team, BingoSession session) {
        super(session);
        this.participant = participant;
        this.team = team;
    }

    public BingoParticipant getParticipant() {
        return participant;
    }

    public BingoTeam getTeam() {
        return team;
    }
}
