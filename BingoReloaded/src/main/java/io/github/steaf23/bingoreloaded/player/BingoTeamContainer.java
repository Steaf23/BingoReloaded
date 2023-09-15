package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
        Set<BingoParticipant> players = new HashSet<>();
        for (BingoTeam activeTeam : teams) {
            players.addAll(activeTeam.getMembers());
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            boolean found = false;
            for (BingoTeam t : teams) {
                for (var player : t.getMembers()) {
                    if (player.getId().equals(p.getUniqueId())) {
                        players.add(player);
                        found = true;
                    }
                }
                if (found)
                    break;
            }
        }
        return players;
    }

    void removeEmptyTeams() {
        teams.removeIf(team -> team.getMembers().size() == 0);
    }

    Set<BingoTeam> getTeams()
    {
        return teams;
    }

    boolean containsId(String teamId)
    {
        return getById(teamId).isPresent();
    }

    boolean contains(BingoTeam team)
    {
        return teams.contains(team);
    }

    Optional<BingoTeam> getById(String teamId)
    {
        return teams.stream().filter(t -> t.getIdentifier().equals(teamId)).findFirst();
    }

    @NotNull
    @Override
    public Iterator<BingoTeam> iterator() {
        return teams.iterator();
    }
}
