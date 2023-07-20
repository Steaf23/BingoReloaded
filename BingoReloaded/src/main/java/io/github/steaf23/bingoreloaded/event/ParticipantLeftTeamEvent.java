package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoTeam;

import javax.annotation.Nullable;

public class ParticipantLeftTeamEvent extends BingoEvent
{
    private final BingoParticipant participant;
    private final BingoTeam team;
    private final boolean leftAutoTeam;

    public ParticipantLeftTeamEvent(BingoParticipant participant, BingoTeam team, BingoSession session) {
        super(session);
        this.participant = participant;
        this.team = team;
        this.leftAutoTeam = false;
    }

    public ParticipantLeftTeamEvent(@Nullable BingoParticipant participant, BingoSession session) {
        super(session);
        this.participant = participant;
        this.team = null;
        this.leftAutoTeam = true;
    }

    public BingoParticipant getParticipant() {
        return participant;
    }

    public BingoTeam getTeam() {
        return team;
    }

    public boolean hasLeftAutoTeam() {
        return leftAutoTeam;
    }
}
