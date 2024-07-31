package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.playerdisplay.scoreboard.TeamPacketHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamDisplay
{
    private record TeamInfo(String identifier, Component displayName, @Nullable Component prefix, @Nullable Component suffix, Collection<String> entries) {}

    private final BingoSession session;
    private final TeamManager manager;
    // A Map of all teams created for each player, used when we have to remove all their teams when leaving or when removing empty teams
    private final Map<UUID, Set<TeamInfo>> createdTeams;

    public TeamDisplay(BingoSession session) {
        this.session = session;
        this.manager = session.teamManager;
        this.createdTeams = new HashMap<>();
    }

    public void update() {
        reset();

        Set<BingoTeam> activeTeams = manager.getActiveTeams().getTeams();
        for (Player player : session.getPlayersInWorld()) { // loop through all actual players.
            addTeamsForPlayer(player, activeTeams);
        }
    }

    /**
     * Creates new entry in teams map if the player was not present before.
     */
    public void addTeamsForPlayer(Player player, Set<BingoTeam> activeTeams) {
        // Compare the cached teams with the actual team manager's team state.
        // - If the manager doesn't have a team that was cached, it means we have to remove this team.

        Set<TeamInfo> knownTeams = createdTeams.getOrDefault(player.getUniqueId(), Set.of());
        for (TeamInfo t : knownTeams) {
            boolean removeTeam = activeTeams.stream().noneMatch(bTeam -> bTeam.getIdentifier().equals(t.identifier()));

            if (removeTeam) {
                removeTeamForPlayer(t.identifier(), player);
            }
        }

        Set<TeamInfo> newTeams = activeTeams.stream().map(this::teamInfoFromBingoTeam).collect(Collectors.toSet());
        createdTeams.put(player.getUniqueId(), newTeams);
        for (TeamInfo team : newTeams) {
            createTeamForPlayer(team, player);
        }
    }

    private TeamInfo teamInfoFromBingoTeam(BingoTeam team) {
        return new TeamInfo(team.getIdentifier(), team.getName(), team.getPrefix(), null, team.getMemberNames());
    }

    private void createTeamForPlayer(TeamInfo team, Player player) {
        TeamPacketHelper.createTeamVisibleToPlayer(player,
                team.identifier(),
                team.displayName(),
                team.prefix(),
                team.suffix(),
                team.entries());
    }

    public void removeTeamForPlayer(String teamIdentifier, Player player) {
        TeamPacketHelper.removeTeamVisibleToPlayer(player, teamIdentifier);
    }

    public void clearTeamsForPlayer(@NotNull Player player) {
        for (TeamInfo info : createdTeams.getOrDefault(player.getUniqueId(), Set.of())) {
            removeTeamForPlayer(info.identifier(), player);
        }
        createdTeams.remove(player.getUniqueId());
    }

    public void reset() {
        for (Player player : session.getPlayersInWorld()) {
            clearTeamsForPlayer(player);
        }
        createdTeams.clear();
    }
}
