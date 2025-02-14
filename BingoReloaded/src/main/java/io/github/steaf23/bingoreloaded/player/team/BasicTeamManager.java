package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantJoinedTeamEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantLeftTeamEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.placeholder.BingoPlaceholderFormatter;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * (This team manager is basic in terms of features, not in terms of complexity)
 */
public class BasicTeamManager implements TeamManager {
    private final BingoSession session;
    private final BingoTeamContainer activeTeams;
    private final TeamData teamData;
    private int maxTeamSize;
    private final Map<String, TeamData.TeamTemplate> joinableTeams;
    private final BingoTeam autoTeam;

    public BasicTeamManager(BingoSession session) {
        this.session = session;
        this.teamData = new TeamData();
        this.activeTeams = new BingoTeamContainer();
        this.maxTeamSize = session.settingsBuilder.view().maxTeamSize();
        this.joinableTeams = teamData.getTeams();
        ConsoleMessenger.log("Loaded " + joinableTeams.size() + " team(s)");

        TextColor autoTeamColor = TextColor.fromHexString("#fdffa8");
        if (autoTeamColor == null) autoTeamColor = NamedTextColor.WHITE;

        this.autoTeam = new BingoTeam("auto", autoTeamColor, BingoMessage.TEAM_AUTO.asPhrase(), createAutoPrefix(autoTeamColor));
    }

    private Component createAutoPrefix(TextColor color) {
        String prefixFormat = new BingoPlaceholderFormatter().getTeamFullFormat();
        return BingoMessage.createPhrase(prefixFormat.replace("{0}", "<" + color.toString() + ">").replace("{1}", "✦") + " ");
    }

    private Component createPrefix(TeamData.TeamTemplate template) {
        String prefixFormat = new BingoPlaceholderFormatter().getTeamFullFormat();
        return BingoMessage.createPhrase(prefixFormat.replace("{0}", "<" + template.color().toString() + ">").replace("{1}", template.stringName()) + " ");
    }

    private void addAutoPlayersToTeams() {
        record TeamCount(BingoTeam team, int count) {
        }

        Optional<BingoTeam> automaticTeamOpt = activeTeams.getTeams().stream().filter(t -> t.getIdentifier().equals("auto")).findFirst();
        if (automaticTeamOpt.isEmpty() || automaticTeamOpt.get().getMembers().isEmpty()) {
            return;
        }

        BingoTeam automaticTeam = automaticTeamOpt.get();
        // FIXME: maybe actually find out what happens if this isn't a copy and how to simplify the code with that information.
        Set<BingoParticipant> automaticTeamPlayers = new HashSet<>(automaticTeam.getMembers());

        int overflowPlayers = getTotalParticipantCapacity() - activeTeams.getAllParticipants().size();
        if (overflowPlayers < 0) {
            ConsoleMessenger.bug("Can not fit every player into a team", this);
            return;
        }

        // 1. create list sorted by how many players are missing from each team using a bit of insertion sorting...
        List<TeamCount> counts = new ArrayList<>();
        for (BingoTeam team : activeTeams.getTeams()) {
            if (team.equals(automaticTeam)) {
                continue;
            }

            TeamCount newCount = new TeamCount(team, team.getMembers().size());
            if (counts.isEmpty()) {
                counts.add(newCount);
                continue;
            }
            int idx = 0;
            for (TeamCount tCount : counts) {
                if (newCount.count <= tCount.count) {
                    counts.add(idx, newCount);
                    break;
                }
                idx++;
            }
            if (idx >= counts.size()) {
                counts.add(newCount);
            }
        }

        // 2. fill this list 1 by 1 using players from the list of queued players.
        // To actually implement this, we need to take the team with the least amount of players,
        //      add a player to it, and then insert it back into the list.
        //      when the team with the least amount of players has the same amount as the biggest team, all teams have been filled.

        Set<BingoParticipant> autoPlayersCopy = new HashSet<>(automaticTeamPlayers);
        // Since we need to remove players from this list as we are iterating, use a direct reference to the iterator.
        for (BingoParticipant participant : autoPlayersCopy) {
            automaticTeam.removeMember(participant);

            TeamCount lowest = !counts.isEmpty() ? counts.getFirst() : null;
            // If our lowest count is the same as the highest count, all incomplete teams have been filled
            if (counts.isEmpty() || lowest.count == getMaxTeamSize()) {
                // If there are still players left in the queue, create a new team
                if (!automaticTeamPlayers.isEmpty()) {
                    BingoTeam newTeam = activateRandomTeam();
                    if (newTeam == null) {
                        ConsoleMessenger.bug("Could not fit every player into a team, since there is not enough room!", this);
                        break;
                    }
                    counts.addFirst(new TeamCount(newTeam, 0));
                    lowest = counts.getFirst();
                }
            }

            counts.removeFirst();
            // After this point in the iteration, lowest will reference the team that will get inserted into counts at the end of the iteration.

            boolean ok = addMemberToTeam(participant, lowest.team.getIdentifier());

            if (ok) {
                lowest = new TeamCount(lowest.team, lowest.count + 1);
                automaticTeamPlayers.remove(participant);
            }

            // Insert the lowest back into the team counts
            int idx = 0;
            for (TeamCount tCount : counts) {
                if (lowest.count <= tCount.count) {
                    counts.add(idx, lowest);
                    break;
                }
                idx++;
            }
            if (idx >= counts.size()) {
                counts.add(lowest);
            }
        }
        automaticTeamPlayers.clear();
    }

    @Override
    public boolean removeMemberFromTeam(@Nullable BingoParticipant member) {
        boolean success = removeMemberFromTeam(member, true);

        if (success && member != null) {
            BingoMessage.LEAVE.sendToAudience(member, NamedTextColor.RED);
        }
        return success;
    }

    public boolean removeMemberFromTeam(@Nullable BingoParticipant player, boolean clearEmptyTeams) {
        if (player == null) return false;

        BingoTeam team = player.getTeam();
        if (getParticipants().contains(player)) {
            team.removeMember(player);
        } else {
            return false;
        }
        if (clearEmptyTeams) {
            activeTeams.removeEmptyTeams("auto");
        }

        var leaveEvent = new ParticipantLeftTeamEvent(player, team, session);
        Bukkit.getPluginManager().callEvent(leaveEvent);
        return true;
    }

    @Override
    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    @Override
    public int getTotalParticipantCapacity() {
        return joinableTeams.size() * getMaxTeamSize();
    }

    @Override
    public int getTeamCount() {
        return activeTeams.teamCount();
    }

    @Override
    public boolean addMemberToTeam(BingoParticipant participant, String teamId) {
        int participantCount = getParticipantCount();

        BingoTeam bingoTeam = teamId.equals("auto") ? autoTeam : activateTeamFromId(teamId);

        if (bingoTeam == null) {
            return false;
        }
        if (bingoTeam.hasMember(participant.getId())) {
            return false;
        }
        // Players should always be able to join the auto team
        if (bingoTeam.getMembers().size() == getMaxTeamSize() && !bingoTeam.equals(autoTeam)) {
            return false;
        }

        // We can only clear empty teams once we added the participant to the new team.
        removeMemberFromTeam(participant, false);
        // If after trying to remove the participant from the teams, the participant count did not change, it means the player was not part of a team already
        if (participantCount == getParticipantCount() && participantCount >= getTotalParticipantCapacity()) {
            ConsoleMessenger.log(Component.text("All teams are full! (" + getParticipantCount() + "/" + getTotalParticipantCapacity() + " players), with a maximum team size of " + getMaxTeamSize()).color(NamedTextColor.RED));
            activeTeams.removeEmptyTeams("auto");
            return false;
        }

        bingoTeam.addMember(participant);

        activeTeams.removeEmptyTeams("auto");

        var joinEvent = new ParticipantJoinedTeamEvent(participant, bingoTeam, session);
        Bukkit.getPluginManager().callEvent(joinEvent);

        if (teamId.equals("auto")) {
            BingoMessage.JOIN_AUTO.sendToAudience(participant, NamedTextColor.GREEN);
        } else {
            BingoMessage.JOIN.sendToAudience(participant, NamedTextColor.GREEN, bingoTeam.getColoredName());
        }
        return true;
    }

    @Override
    public Map<String, TeamData.TeamTemplate> getJoinableTeams() {
        return teamData.getTeams();
    }

    @Override
    public BingoTeamContainer getActiveTeams() {
        return activeTeams;
    }

    @Nullable
    private BingoTeam activateTeamFromId(String teamId) {
        TeamData.TeamTemplate team = joinableTeams.getOrDefault(teamId, null);
        if (team == null) {
            return null;
        }

        if (session.isRunning() && !activeTeams.containsId(teamId)) {
            return null;
        }

        Optional<BingoTeam> existingTeam = activeTeams.getById(teamId);
        if (existingTeam.isPresent())
            return existingTeam.get();

        BingoTeam bTeam = new BingoTeam(teamId, team.color(), team.nameComponent(), createPrefix(team));

        activeTeams.addTeam(bTeam);
        return bTeam;
    }

    private @Nullable BingoTeam activateRandomTeam() {
        List<String> allTeams = new ArrayList<>(teamData.getTeams().keySet());
        Collections.shuffle(allTeams);

        for (String teamId : allTeams) {
            if (!activeTeams.containsId(teamId)) {
                return activateTeamFromId(teamId);
            }
        }

        return null;
    }

    @Override
    public int getParticipantCount() {
        return activeTeams.getAllParticipants().size();
    }

    @Override
    public void setup() {
        addAutoPlayersToTeams();
        activeTeams.removeTeam(autoTeam);
        activeTeams.removeEmptyTeams("auto");
    }

    @Override
    public void reset() {
        activeTeams.addTeam(autoTeam);
    }

    //== EventHandlers ==========================================
    @Override
    public void handleSettingsUpdated(BingoSettingsUpdatedEvent event) {
        int newTeamSize = event.getNewSettings().maxTeamSize();
        if (newTeamSize == getMaxTeamSize())
            return;

        if (maxTeamSize < newTeamSize) {
            maxTeamSize = newTeamSize;
            return;
        }

        maxTeamSize = newTeamSize;
        if (!session.isRunning()) {
            getParticipants().forEach(p -> addMemberToTeam(p, "auto"));
            BingoMessage.TEAM_SIZE_CHANGED.sendToAudience(session, NamedTextColor.RED);
        }
    }

    @Override
    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {
        ConsoleMessenger.log(event.getPlayer().displayName().append(Component.text(" left world")).color(NamedTextColor.GOLD), session.getOverworld().getName());
    }

    @Override
    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event) {
        ConsoleMessenger.log(event.getPlayer().displayName().append(Component.text(" joined world")).color(NamedTextColor.GOLD), session.getOverworld().getName());

        BingoParticipant participant = getPlayerAsParticipant(event.getPlayer());
        if (participant != null) {
            if (!session.isRunning()) {
                return;
            }
            BingoMessage.JOIN.sendToAudience(participant, NamedTextColor.GREEN, participant.getTeam().getColoredName());
            return;
        }

        if (session.isRunning()) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            if (session.getPluginConfig().getOptionValue(BingoOptions.ALLOW_VIEWING_ALL_CARDS)) {
                BingoMessage.SPECTATOR_JOIN.sendToAudience(event.getPlayer());
            }
            else {
                BingoMessage.SPECTATOR_JOIN_NO_VIEW.sendToAudience(event.getPlayer());
            }

            return;
        }

        addMemberToTeam(new BingoPlayer(event.getPlayer(), session), "auto");
    }
}
