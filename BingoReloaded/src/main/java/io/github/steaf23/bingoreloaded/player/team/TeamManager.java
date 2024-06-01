package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.gameloop.SessionMember;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.Set;

public interface TeamManager extends SessionMember
{
    default int getParticipantCount() {
        return getParticipants().size();
    }

    default Set<BingoParticipant> getParticipants() {
        return getActiveTeams().getAllParticipants();
    }

    default Set<BingoParticipant> getOnlineParticipants() {
        return getActiveTeams().getAllOnlineParticipants();
    }

    Map<String, TeamData.TeamTemplate> getJoinableTeams();

    BingoTeamContainer getActiveTeams();

    @Nullable
    default BingoParticipant getPlayerAsParticipant(@NonNull Player player) {
        for (BingoParticipant participant : getParticipants()) {
            if (participant.getId().equals(player.getUniqueId())) {
                return participant;
            }
        }
        return null;
    }

    default int getTeamCount() {
        return getActiveTeams().teamCount();
    }

    /**
     * @param player
     * @param teamId
     * @return true if the player could be added to the team successfully
     */
    boolean addMemberToTeam(BingoParticipant player, String teamId);

    /**
     * @param member
     * @return true if the player could be removed from all teams successfully
     */
    boolean removeMemberFromTeam(@Nullable BingoParticipant member);

    int getMaxTeamSize();

    int getCapacity();


    default void handleSettingsUpdated(final BingoSettingsUpdatedEvent event) {
    }

    default void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event) {
    }

    default void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {
    }

}
