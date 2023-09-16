package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Similar to TeamManager but each team can only have 1 member, the team's name being the name of the member.
 */
public class SoloTeamManager {
    private BingoTeamContainer teams;
    private Scoreboard teamBoard;

    void addPlayer(BingoParticipant participant) {
        ChatColor teamColor = determineTeamColor();
        BingoTeam team = new BingoTeam(teamBoard.registerNewTeam(participant.getDisplayName()), teamColor, participant.getDisplayName());
        teams.addTeam(team);
    }

    void removePlayer(BingoParticipant participant) {
        for (BingoTeam team : teams) {
            if (team.getMembers().contains(participant)) {
                team.removeMember(participant);
            }
        }
        teams.removeEmptyTeams();
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

    ChatColor determineTeamColor() {
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
        //TODO: create color from hue value

        return ChatColor.of("#004400");
    }
}


