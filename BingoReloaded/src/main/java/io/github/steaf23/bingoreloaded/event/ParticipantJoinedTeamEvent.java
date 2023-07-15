package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ParticipantJoinedTeamEvent extends BingoEvent
{
    private final BingoParticipant participant;
    private final BingoTeam team;

    private final boolean joinedAutoTeam;

    public ParticipantJoinedTeamEvent(@NotNull BingoParticipant participant, @Nullable BingoTeam team, BingoSession session) {
        super(session);
        this.participant = participant;
        this.team = team;
        this.joinedAutoTeam = false;
    }

    public ParticipantJoinedTeamEvent(BingoParticipant participant, BingoSession session) {
        super(session);
        this.participant = participant;
        this.team = null;
        this.joinedAutoTeam = true;
    }

    public BingoParticipant getParticipant() {
        return participant;
    }

    public BingoTeam getTeam() {
        return team;
    }

    public boolean hasJoinedAutoTeam() {
        return joinedAutoTeam;
    }
}
