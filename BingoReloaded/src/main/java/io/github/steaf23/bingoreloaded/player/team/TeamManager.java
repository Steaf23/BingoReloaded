package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.gameloop.SessionMember;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;

public interface TeamManager extends SessionMember
{
    default public Set<BingoParticipant> getParticipants()
    {
        return getActiveTeams().getAllParticipants();
    }

    public TeamData getTeamData();

    public BingoTeamContainer getActiveTeams();

    @Nullable
    default public BingoParticipant getPlayerAsParticipant(@NonNull Player player)
    {
        if (player == null) return null;

        for (BingoParticipant participant : getParticipants()) {
            if (participant.getId().equals(player.getUniqueId())) {
                return participant;
            }
        }
        return null;
    }

    default public int getTeamCount()
    {
        return getActiveTeams().teamCount();
    }

    /**
     * @param player
     * @param teamId
     * @return true if the player could be added to the team successfully
     */
    public boolean addMemberToTeam(BingoParticipant player, String teamId);

    /**
     * @param member
     * @return true if the player could be removed from all teams successfully
     */
    public boolean removeMemberFromTeam(@Nullable BingoParticipant member);

    public int getMaxTeamSize();

    default public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event) {}

    default public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event) {}

    default public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {}

}
