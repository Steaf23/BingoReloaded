package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public void removeTeam(BingoTeam team) {
        teams.remove(team);
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
        return allPlayers;
    }

    public Set<BingoParticipant> getAllOnlineParticipants() {
        return getAllParticipants().stream()
                .filter(p -> p.sessionPlayer().isPresent() || p.alwaysActive())
                .collect(Collectors.toSet());
    }

    public long getOnlineTeamCount() {
        return teams.stream()
                .filter(t -> t.getMembers().stream().anyMatch(player -> player.sessionPlayer().isPresent() || player.alwaysActive()))
                .count();
    }

    public void removeEmptyTeams(String... exceptions) {
        teams.removeIf(team -> team.getMembers().isEmpty() && Arrays.stream(exceptions).noneMatch(Predicate.isEqual(team.getIdentifier())));
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
