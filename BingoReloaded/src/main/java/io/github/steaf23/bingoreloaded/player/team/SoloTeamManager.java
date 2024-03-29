package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.event.ParticipantJoinedTeamEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.VirtualBingoPlayer;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.*;
import java.util.Optional;
import java.util.Set;

/**
 * Similar to TeamManager but each team can only have 1 member, the team's name being the name of the member.
 */
public class SoloTeamManager implements TeamManager
{
    private final BingoTeamContainer teams;
    private final Scoreboard teamBoard;
    private final BingoSession session;
    private final TeamData teamData;
    private Set<BingoParticipant> joinedPlayers;

    public SoloTeamManager(Scoreboard teamBoard, BingoSession session)
    {
        this.teamBoard = teamBoard;
        this.session = session;
        this.teamData = new TeamData();
        this.teams = new BingoTeamContainer();
    }

    @Nullable
    BingoParticipant getPlayer(BingoTeam team) {
        Optional<BingoParticipant> participant = team.getMembers().stream().findFirst();
        if (participant.isEmpty()) {
            Message.error("Team " + team.getColoredName().asLegacyString() + "does not have a player!");
            return null;
        }
        return team.getMembers().stream().findFirst().get();
    }

    @Nullable
    BingoTeam getTeamOfPlayer(BingoParticipant participant) {
        for (BingoTeam team : teams) {
            if (team.getMembers().contains(participant)) {
                return team;
            }
        }
        return null;
    }

    private ChatColor determineTeamColor() {
        // pick a new color based on participant count,
        // works kinda like how you choose pivots for quicksort in that no 2 similar colors should be selected one after another
        int max = 256;

        int divider = 1;
        int multiplier = 1;

        int amount = (teams.teamCount() % max) + 1;
        for (int i = 0; i < amount; i++) {
            if (divider > 1) {
                multiplier += 2;
            }
            if (i >= divider) {
                divider *= 2;
                multiplier = 1;
            }
        }
        int hue = max / divider * multiplier;

        Color col = Color.getHSBColor(hue / 256.0f, 0.7f, 1.0f);
        return ChatColor.of(col);
    }

    @Override
    public @Nullable BingoSession getSession() {
        return session;
    }

    @Override
    public void setup() {

    }

    @Override
    public TeamData getTeamData() {
        return teamData;
    }

    @Override
    public BingoTeamContainer getActiveTeams() {
        return teams;
    }

    /**
     * We don't care about the team unless it's auto, else we just add a new team
     * @param player
     * @param teamId
     * @return
     */
    @Override
    public boolean addMemberToTeam(BingoParticipant player, String teamId) {
        if (teamId.equals("auto")) {
            return false;
        }

        removeMemberFromTeam(player);
        ChatColor teamColor = determineTeamColor();
        Team boardTeam = teamBoard.getTeam(player.getId().toString());
        if (boardTeam == null) {
            boardTeam = teamBoard.registerNewTeam(player.getId().toString());
        }
        String displayName = player.getDisplayName();
        if (player instanceof VirtualBingoPlayer fakePlayer)
        {
            displayName = fakePlayer.getName();
        }
        BingoTeam team = new BingoTeam(boardTeam, teamColor, displayName);
        team.addMember(player);
        teams.addTeam(team);

        var joinEvent = new ParticipantJoinedTeamEvent(player, team, session);
        Bukkit.getPluginManager().callEvent(joinEvent);
        return true;
    }

    @Override
    public boolean removeMemberFromTeam(@Nullable BingoParticipant member) {
        for (BingoTeam team : teams) {
            if (team.getMembers().contains(member)) {
                team.removeMember(member);
                team.getScoreboardTeam().unregister();
            }
        }
        teams.removeEmptyTeams();
        return false;
    }

    @Override
    public int getMaxTeamSize() {
        return session.settingsBuilder.view().maxTeamSize();
    }
}


