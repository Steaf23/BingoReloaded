package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.api.BingoEvents;
import io.github.steaf23.bingoreloaded.gameloop.SessionMember;
import io.github.steaf23.bingoreloaded.lib.api.InteractAction;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import org.jetbrains.annotations.Nullable;

public interface GamePhase extends SessionMember
{
    /**
     * To be called when this phase needs to (forcefully) end.
     */
    void end();
    void handlePlayerJoinedSessionWorld(PlayerHandle player);
    void handlePlayerLeftSessionWorld(PlayerHandle player);
    void handleSettingsUpdated(final BingoSettings newSettings);
    EventResult<?> handlePlayerInteracted(PlayerHandle player, @Nullable StackHandle stack, InteractAction action);

    default void handleParticipantJoinedTeam(final BingoEvents.TeamParticipantEvent event) {};
    default void handleParticipantLeftTeam(final BingoEvents.TeamParticipantEvent event) {};
}
