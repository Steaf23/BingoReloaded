package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.api.BingoEvents;
import io.github.steaf23.bingoreloaded.gameloop.SessionMember;
import io.github.steaf23.bingoreloaded.lib.api.InteractAction;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.StackHandle;

public interface GamePhase extends SessionMember
{
    /**
     * To be called when this phase needs to (forcefully) end.
     */
    void end();
    void handlePlayerJoinedSessionWorld(final BingoEvents.PlayerEvent event);
    void handlePlayerLeftSessionWorld(final BingoEvents.PlayerEvent event);
    void handleSettingsUpdated(final BingoEvents.SettingsUpdated event);
    boolean handlePlayerInteract(final PlayerHandle player, StackHandle stack, InteractAction action);

    default void handleParticipantJoinedTeam(final BingoEvents.TeamParticipantEvent event) {};
    default void handleParticipantLeftTeam(final BingoEvents.TeamParticipantEvent event) {};
}
