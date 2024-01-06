package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BingoTeamContainer implements Iterable<BingoTeam>
{
    private final Set<BingoTeam> teams;

    BingoTeamContainer() {
        teams = new HashSet<>();
    }

    public int teamCount() {
        return teams.size();
    }

    public void addTeam(BingoTeam team) {
        teams.add(team);
    }

    public BingoTeam getLeadingTeam() {
        Optional<BingoTeam> leadingTeam = teams.stream().max(
                Comparator.comparingInt(BingoTeam::getCompleteCount)
        );
        return leadingTeam.orElse(null);
    }

    public BingoTeam getLosingTeam() {
        Optional<BingoTeam> losingTeam = teams.stream().min(
                Comparator.comparingInt(BingoTeam::getCompleteCount)
        );
        return losingTeam.orElse(null);
    }

    public int getTotalCompleteCount()
    {
        int count = 0;
        for (BingoTeam team : this) {
            count += team.getCompleteCount();
        }
        return count;
    }

    public Set<BingoParticipant> getAllParticipants() {
        Set<BingoParticipant> allPlayers = new HashSet<>();
        for (BingoTeam activeTeam : teams) {
            allPlayers.addAll(activeTeam.getMembers());
        }

        Set<BingoParticipant> playingPlayers = new HashSet<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            boolean found = false;
            for (BingoTeam t : teams) {
                for (var player : t.getMembers()) {
                    if (player.getId().equals(p.getUniqueId())) {
                        playingPlayers.add(player);
                        found = true;
                        break;
                    }
                }
                if (found)
                    break;
            }
        }
        return playingPlayers;
    }

    public void removeEmptyTeams() {
        teams.removeIf(team -> team.getMembers().size() == 0);
    }

    public Set<BingoTeam> getTeams()
    {
        return teams;
    }

    public boolean containsId(String teamId)
    {
        return getById(teamId).isPresent();
    }

    public boolean contains(BingoTeam team)
    {
        return teams.contains(team);
    }

    public Optional<BingoTeam> getById(String teamId)
    {
        return teams.stream().filter(t -> t.getIdentifier().equals(teamId)).findFirst();
    }

    @NotNull
    @Override
    public Iterator<BingoTeam> iterator() {
        return teams.iterator();
    }
}
