package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.VirtualBingoPlayer;
import io.github.steaf23.bingoreloaded.placeholder.BingoPlaceholderFormatter;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

/**
 * (This team manager is basic in terms of features, not in terms of complexity)
 */
public class BasicTeamManager implements TeamManager
{
    private final BingoSession session;
    private final BingoTeamContainer activeTeams;
    private final TeamData teamData;
    private int maxTeamSize;
    private Map<String, TeamData.TeamTemplate> joinableTeams;
    private final BingoTeam autoTeam;

    public BasicTeamManager(BingoSession session) {
        this.session = session;
        this.teamData = new TeamData();
        this.activeTeams = new BingoTeamContainer();
        this.maxTeamSize = session.settingsBuilder.view().maxTeamSize();
        this.joinableTeams = teamData.getTeams();
        Message.log("Loaded " + joinableTeams.size() + " team(s)");

        this.autoTeam = new BingoTeam("auto", ChatColor.of("#fdffa8"), BingoTranslation.TEAM_AUTO.translate(), createAutoPrefix(ChatColor.of("#fdffa8")));
    }

    private BaseComponent createAutoPrefix(ChatColor color) {
        String prefixFormat = new BingoPlaceholderFormatter().getTeamFullFormat();
        BaseComponent prefix = TextComponent.fromLegacy(BingoPlaceholderFormatter.createLegacyTextFromMessage(prefixFormat, color.toString(), "✦") + " ");
        return prefix;
    }

    private BaseComponent createPrefix(TeamData.TeamTemplate template) {
        String prefixFormat = new BingoPlaceholderFormatter().getTeamFullFormat();
        BaseComponent prefix = TextComponent.fromLegacy(BingoPlaceholderFormatter.createLegacyTextFromMessage(prefixFormat, template.color().toString(), template.name()) + " ");
        return prefix;
    }

    @Nullable
    public VirtualBingoPlayer getVirtualPlayerFromName(String playerName) {
        for (BingoParticipant participant : getParticipants()) {
            if (!(participant instanceof VirtualBingoPlayer virtualPlayer)) {
                continue;
            }

            if (virtualPlayer.getName().equals(playerName)) {
                return virtualPlayer;
            }
        }
        return null;
    }

    private void addAutoPlayersToTeams() {
        record TeamCount(BingoTeam team, int count)
        {
        }

        BingoTeam automaticTeam = activeTeams.getTeams().stream().filter(t -> t.getIdentifier().equals("auto")).findFirst().get();
        if (automaticTeam == null || automaticTeam.getMembers().size() == 0) {
            return;
        }

        // FIXME: maybe actually find out what happens if this isn't a copy and how to simplify the code with that information.
        Set<BingoParticipant> automaticTeamPlayers = new HashSet<BingoParticipant>(automaticTeam.getMembers());

        int availablePlayers = getTotalParticipantCapacity() - activeTeams.getAllParticipants().size();
        if (automaticTeamPlayers.size() > availablePlayers) {
            Message.error("Could not fit every player into a team (Please report!)");
            return;
        }

        // 1. create list sorted by how many players are missing from each team using a bit of insertion sorting...
        List<TeamCount> counts = new ArrayList<>();
        for (BingoTeam team : activeTeams.getTeams()) {
            if (team.equals(automaticTeam)) {
                continue;
            }

            TeamCount newCount = new TeamCount(team, team.getMembers().size());
            if (counts.size() == 0) {
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

            TeamCount lowest = counts.size() > 0 ? counts.get(0) : null;
            // If our lowest count is the same as the highest count, all incomplete teams have been filled
            if (counts.size() == 0 || lowest.count == getMaxTeamSize()) {
                // If there are still players left in the queue, create a new team
                if (automaticTeamPlayers.size() > 0) {
                    BingoTeam newTeam = activateAnyTeam();
                    if (newTeam == null) {
                        Message.error("Could not fit every player into a team, since there is not enough room!");
                        break;
                    }
                    counts.add(0, new TeamCount(newTeam, 0));
                    lowest = counts.get(0);
                }
            }

            counts.remove(0);
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
        return removeMemberFromTeam(member, true);
    }

    public boolean removeMemberFromTeam(@Nullable BingoParticipant player, boolean clearEmptyTeams) {
        if (player == null) return false;

        BingoTeam team = player.getTeam();
        if (getParticipants().contains(player)) {
            player.getTeam().removeMember(player);
        } else {
            return false;
        }
        if (clearEmptyTeams) {
            activeTeams.removeEmptyTeams("auto");
        }

        var leaveEvent = new ParticipantLeftTeamEvent(player, team, session);
        Bukkit.getPluginManager().callEvent(leaveEvent);

        player.sessionPlayer().ifPresent(p -> {
            new TranslatedMessage(BingoTranslation.LEAVE).color(ChatColor.RED).send(p);
        });
        return true;
    }

    @Override
    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    @Override
    public int getTotalParticipantCapacity() {
        int totalPlayers = joinableTeams.size() - 1 * getMaxTeamSize();
        return totalPlayers;
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
        if (bingoTeam.getMembers().size() == getMaxTeamSize()) {
            return false;
        }

        // We can only clear empty teams once we added the participant to the new team.
        removeMemberFromTeam(participant, false);
        if (participantCount == getParticipantCount() && participantCount >= getTotalParticipantCapacity()) {
            //TODO: translate this
            Message.log(ChatColor.RED + "All teams are full!");
            activeTeams.removeEmptyTeams("auto");
            return false;
        }

        bingoTeam.addMember(participant);

        activeTeams.removeEmptyTeams("auto");

        var joinEvent = new ParticipantJoinedTeamEvent(participant, bingoTeam, session);
        Bukkit.getPluginManager().callEvent(joinEvent);

        if (teamId.equals("auto")) {
            participant.sessionPlayer().ifPresent(p -> {
                new TranslatedMessage(BingoTranslation.JOIN_AUTO).color(ChatColor.GREEN)
                        .send(p);
            });
        } else {
            participant.sessionPlayer().ifPresent(p -> {
                new TranslatedMessage(BingoTranslation.JOIN).color(ChatColor.GREEN)
                        .arg(bingoTeam.getColoredName())
                        .send(p);
            });
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

        BingoTeam bTeam = new BingoTeam(teamId, team.color(), team.name(), createPrefix(team));

        activeTeams.addTeam(bTeam);
        return bTeam;
    }

    private @Nullable BingoTeam activateAnyTeam() {
        for (String teamId : teamData.getTeams().keySet()) {
            if (!activeTeams.containsId(teamId)) {
                return activateTeamFromId(teamId);
            }
        }

        return null;
    }

    private int getOnlineParticipantCount() {
        return getActiveTeams().getAllOnlineParticipants().size();
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
            getParticipants().forEach(p -> {
                addMemberToTeam(p, "auto");
                p.sessionPlayer().ifPresent(gamePlayer ->
                        new TranslatedMessage(BingoTranslation.TEAM_SIZE_CHANGED)
                                .color(ChatColor.RED)
                                .send(gamePlayer));
            });
        }
    }

    @Override
    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {
        Message.log(ChatColor.GOLD + event.getPlayer().getDisplayName() + " left world", session.getOverworld().getName());
    }

    @Override
    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event) {
        Message.log(ChatColor.GOLD + event.getPlayer().getDisplayName() + " joined world", session.getOverworld().getName());

        BingoParticipant participant = getPlayerAsParticipant(event.getPlayer());
        if (participant != null) {
            participant.sessionPlayer().ifPresent(player -> {
                if (!session.isRunning()) {
                    return;
                }
                new TranslatedMessage(BingoTranslation.JOIN).color(ChatColor.GREEN)
                        .arg(participant.getTeam().getColoredName())
                        .send(player);
            });
            return;
        }

        if (session.isRunning()) {
            new TranslatedMessage(BingoTranslation.NO_JOIN).send(event.getPlayer());
            return;
        }

        if (getPlayerAsParticipant(event.getPlayer()) == null) {
            addMemberToTeam(new BingoPlayer(event.getPlayer(), session), "auto");
        }
    }
}
