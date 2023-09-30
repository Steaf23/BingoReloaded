package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.TeamSelectionMenu;
import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedSelectionMenu;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.VirtualBingoPlayer;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

/**
 * (This team manager is basic in terms of features, not in terms of complexity)
 */
public class BasicTeamManager implements TeamManager
{
    private final BingoSession session;
    private final BingoTeamContainer activeTeams;
    private final Scoreboard teams;
    private final TeamData teamData;

    // Contains all players that will join a team automatically when the game starts
    private final Map<UUID, String> automaticTeamPlayers;
    private int maxTeamSize;

    public BasicTeamManager(Scoreboard teamBoard, BingoSession session) {
        this.session = session;
        this.teams = teamBoard;
        this.teamData = new TeamData();
        this.maxTeamSize = session.settingsBuilder.view().maxTeamSize();
        this.automaticTeamPlayers = new HashMap<>();
        this.activeTeams = new BingoTeamContainer();
        createTeams();
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

        int totalPlayers = teams.getTeams().size() * maxTeamSize;
        int availablePlayers = totalPlayers - getTotalParticipantCount();
        if (automaticTeamPlayers.size() > availablePlayers) {
            Message.error("Could not fit every player into a team, consider changing the team size or adding more teams");
            return;
        }

        // 1. create list sorted by how many players are missing from each team using a bit of insertion sorting...
        List<TeamCount> counts = new ArrayList<>();
        for (BingoTeam team : activeTeams.getTeams()) {
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

        HashMap<UUID, String> autoPlayersCopy = new HashMap<>(automaticTeamPlayers);
        // Since we need to remove players from this list as we are iterating, use a direct reference to the iterator.
        for (UUID playerId : autoPlayersCopy.keySet()) {
            String playerName = autoPlayersCopy.get(playerId);
            TeamCount lowest = counts.size() > 0 ? counts.get(0) : null;
            // If our lowest count is the same as the highest count, all incomplete teams have been filled
            if (counts.size() == 0 || lowest.count == maxTeamSize) {
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

            // Create a Substitute player when the uuid is invalid for some reason.
            boolean ok = false;
            if (Bukkit.getPlayer(playerId) != null) {
                BingoPlayer player = new BingoPlayer(Bukkit.getPlayer(playerId), session);
                ok = addMemberToTeam(player, lowest.team.getIdentifier());
            } else {
                VirtualBingoPlayer virtualPlayer = new VirtualBingoPlayer(playerId, playerName, session);
                ok = addMemberToTeam(virtualPlayer, lowest.team.getIdentifier());
            }

            if (ok) {
                lowest = new TeamCount(lowest.team, lowest.count + 1);
                automaticTeamPlayers.remove(playerId);
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
    public boolean removeMemberFromTeam(@Nullable BingoParticipant player) {
        if (player == null) return false;

        automaticTeamPlayers.remove(player.getId());

        if (getParticipants().contains(player)) {
            player.getTeam().removeMember(player);
        } else {
            return false;
        }

        var leaveEvent = new ParticipantLeftTeamEvent(player, player.getTeam(), session);
        Bukkit.getPluginManager().callEvent(leaveEvent);
        return true;
    }

    @Override
    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    @Override
    public int getTeamCount() {
        return activeTeams.teamCount();
    }

    @Override
    public boolean addMemberToTeam(BingoParticipant participant, String teamId) {
        if (teamId.equals("auto")) {
            removeMemberFromTeam(participant);

            if (automaticTeamPlayers.containsKey(participant.getId())) {
                return false;
            }

            automaticTeamPlayers.put(participant.getId(), participant.getDisplayName());
            participant.sessionPlayer().ifPresent(p -> {
                new TranslatedMessage(BingoTranslation.JOIN_AUTO).color(ChatColor.GREEN)
                        .send(p);
            });

            var joinEvent = new ParticipantJoinedTeamEvent(participant, session);
            Bukkit.getPluginManager().callEvent(joinEvent);
            return true;
        }

        BingoTeam bingoTeam = activateTeamFromId(teamId);

        if (bingoTeam.hasMember(participant.getId())) {
            return false;
        }
        if (bingoTeam == null) {
            return false;
        }
        if (bingoTeam.getMembers().size() == maxTeamSize) {
            return false;
        }

        removeMemberFromTeam(participant);
        automaticTeamPlayers.remove(participant.getId());

        participant.sessionPlayer().ifPresent(p -> {
            new TranslatedMessage(BingoTranslation.JOIN).color(ChatColor.GREEN)
                    .arg(bingoTeam.getColoredName().asLegacyString())
                    .send(p);
        });

        bingoTeam.addMember(participant);

        var joinEvent = new ParticipantJoinedTeamEvent(participant, bingoTeam, session);
        Bukkit.getPluginManager().callEvent(joinEvent);
        return true;
    }

    @Override
    public TeamData getTeamData() {
        return teamData;
    }

    @Override
    public BingoTeamContainer getActiveTeams() {
        return activeTeams;
    }

    @Nullable
    private BingoTeam activateTeamFromId(String teamId) {
        Team team = teams.getTeam(teamId);
        if (team == null) {
            return null;
        }

        if (session.isRunning() && !activeTeams.containsId(teamId)) {
            return null;
        }

        Optional<BingoTeam> existingTeam = activeTeams.getById(team.getName());
        if (existingTeam.isPresent())
            return existingTeam.get();

        TeamData.TeamTemplate template = teamData.getTeam(team.getName());
        BingoTeam bTeam = new BingoTeam(team, template.color(), template.name());

        activeTeams.addTeam(bTeam);
        return bTeam;
    }

    private void createTeams() {
        var savedTeams = teamData.getTeams();
        for (String team : savedTeams.keySet()) {
            TeamData.TeamTemplate template = savedTeams.get(team);

            String name = team;
            Team t = teams.registerNewTeam(name);
            String prefix = "" + ChatColor.DARK_RED + "[" + template.color() + ChatColor.BOLD + template.name() + ChatColor.DARK_RED + "] ";
            t.setPrefix(prefix);
            // Add dummy entry to show the prefix on the board
            t.addEntry("" + team);
        }
        Message.log("Successfully created 16 teams");
    }

    private BingoTeam activateAnyTeam() {
        for (String teamId : teamData.getTeams().keySet()) {
            if (!activeTeams.containsId(teamId)) {
                return activateTeamFromId(teamId);
            }
        }

        return null;
    }

    private int getTotalParticipantCount() {
        return getParticipants().size() + automaticTeamPlayers.size();
    }

    @Override
    public @Nullable BingoSession getSession() {
        return session;
    }

    @Override
    public void setup() {
        addAutoPlayersToTeams();
        // update active players
        for (BingoParticipant participant : activeTeams.getAllParticipants()) {
            if (participant.sessionPlayer().isEmpty() && !participant.alwaysActive()) {
                removeMemberFromTeam(participant);
            }
        }
        activeTeams.removeEmptyTeams();
    }

    //== EventHandlers ==========================================
    @Override
    public void handleSettingsUpdated(BingoSettingsUpdatedEvent event) {
        int newTeamSize = event.getNewSettings().maxTeamSize();
        if (newTeamSize == maxTeamSize)
            return;

        this.maxTeamSize = newTeamSize;
        if (!session.isRunning()) {
            getParticipants().forEach(p -> {
                removeMemberFromTeam(p);
                p.sessionPlayer().ifPresent(gamePlayer ->
                        new TranslatedMessage(BingoTranslation.TEAM_SIZE_CHANGED)
                                .color(ChatColor.RED)
                                .send(gamePlayer));
            });
        }
    }

    @Override
    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {
        BingoParticipant participant = getPlayerAsParticipant(event.getPlayer());
        if (participant == null)
            return;

        participant.getTeam().team.removeEntry(event.getPlayer().getName());
    }

    @Override
    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event) {
        BingoParticipant participant = getPlayerAsParticipant(event.getPlayer());
        if (participant == null) {
            if (!session.isRunning()) {
                BingoPlayer player = new BingoPlayer(event.getPlayer(), session);
                addMemberToTeam(player, "auto");
            }
            return;
        }

        participant.getTeam().team.addEntry(event.getPlayer().getName());
    }
}
