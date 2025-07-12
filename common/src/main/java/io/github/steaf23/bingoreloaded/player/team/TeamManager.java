package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.api.BingoEvents;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface TeamManager
{
    void setup();
    void reset();

    default int getParticipantCount() {
        return getParticipants().size();
    }

    default Set<BingoParticipant> getParticipants() {
        return getActiveTeams().getAllParticipants();
    }

    /**
     * @return map of team identifiers and templates for all teams can be joined by the player when trying to join the game.
     * Used by the team selection menu.
     */
    Map<String, TeamData.TeamTemplate> getJoinableTeams();

    BingoTeamContainer getActiveTeams();

    /**
     * Attempts to retrieve the given player as a BingoParticipant.
     * Player does not have to be in the correct session world for this to work.
     * If this player is not yet participating, null will be returned instead
     */
    @Nullable
    default BingoParticipant getPlayerAsParticipant(@NotNull PlayerHandle player) {
        for (BingoParticipant participant : getParticipants()) {
            if (participant.getId().equals(player.uniqueId())) {
                return participant;
            }
        }
        return null;
    }

    default int getTeamCount() {
        return getActiveTeams().teamCount();
    }

    /**
     * @return true if the player could be added to the team successfully
     */
    boolean addMemberToTeam(BingoParticipant player, String teamId);

    /**
     * @return true if the player could be removed from all teams successfully
     */
    boolean removeMemberFromTeam(@Nullable BingoParticipant member);

    int getMaxTeamSize();

    int getTotalParticipantCapacity();


    default void handleSettingsUpdated(final BingoSettings newSettings) {
    }

    default void handlePlayerJoinedSessionWorld(final BingoEvents.PlayerSessionEvent event) {
    }

    default void handlePlayerLeftSessionWorld(final BingoEvents.PlayerSessionEvent event) {
    }

}
