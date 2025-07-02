package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantJoinedTeamEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantLeftTeamEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.gameloop.SessionMember;

public interface GamePhase extends SessionMember
{
    /**
     * To be called when this phase needs to (forcefully) end.
     */
    void end();
    void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event);
    void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event);
    void handleSettingsUpdated(final BingoSettingsUpdatedEvent event);
    void handlePlayerInteract(final PlayerInteractEvent event);

    default void handleParticipantJoinedTeam(final ParticipantJoinedTeamEvent event) {};
    default void handleParticipantLeftTeam(final ParticipantLeftTeamEvent event) {};
}
