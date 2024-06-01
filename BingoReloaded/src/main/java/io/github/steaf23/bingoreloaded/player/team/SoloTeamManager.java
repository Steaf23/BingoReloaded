package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.event.ParticipantCountChangedEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantJoinedTeamEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.VirtualBingoPlayer;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Similar to BasicTeamManager but each team can only have 1 member, the team's name being the name of the member.
 */
public class SoloTeamManager implements TeamManager
{
    private final BingoTeamContainer teams;
    private final Scoreboard teamBoard;
    private final BingoSession session;
    private final TeamData teamData;

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
            Message.error("Team " + team.getColoredName().toLegacyText() + "does not have a player!");
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
    public Map<String, TeamData.TeamTemplate> getJoinableTeams() {
        return Map.of();
    }

    @Override
    public BingoTeamContainer getActiveTeams() {
        return teams;
    }

    /**
     * @param player player to create a team for
     * @param teamId ignored for solo team manager, since teams are managed per player.
     * @return
     */
    @Override
    public boolean addMemberToTeam(BingoParticipant player, String teamId) {
        removeMemberFromTeamSilently(player);

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

        player.sessionPlayer().ifPresent(p -> {
            new TranslatedMessage(BingoTranslation.JOIN).color(ChatColor.GREEN)
                    .arg(team.getColoredName())
                    .send(p);
        });

        var joinEvent = new ParticipantJoinedTeamEvent(player, team, session);
        Bukkit.getPluginManager().callEvent(joinEvent);
        return true;
    }

    @Override
    public boolean removeMemberFromTeam(@Nullable BingoParticipant member) {
        removeMemberFromTeamSilently(member);
        if (member == null) {
            return true;
        }

        member.sessionPlayer().ifPresent(player -> {
            new TranslatedMessage(BingoTranslation.LEAVE).color(ChatColor.RED).send(player);
        });

        return true;
    }

    @Override
    public int getMaxTeamSize() {
        return 1;
    }

    @Override
    public int getCapacity() {
        return Integer.MAX_VALUE;
    }


    @Override
    public void handlePlayerJoinedSessionWorld(PlayerJoinedSessionWorldEvent event) {
        BingoParticipant participant = getPlayerAsParticipant(event.getPlayer());
        if (participant == null)
            return;

        int onlineParticipants = getOnlineParticipants().size();
        Event e = new ParticipantCountChangedEvent(session, onlineParticipants, onlineParticipants - 1);
        Bukkit.getPluginManager().callEvent(e);
    }

    @Override
    public void handlePlayerLeftSessionWorld(PlayerLeftSessionWorldEvent event) {
        BingoParticipant participant = getPlayerAsParticipant(event.getPlayer());
        if (participant == null)
            return;

        int onlineParticipants = getOnlineParticipants().size();
        Event e = new ParticipantCountChangedEvent(session, onlineParticipants, onlineParticipants + 1);
        Bukkit.getPluginManager().callEvent(e);
    }

    private void removeMemberFromTeamSilently(@NotNull BingoParticipant member) {
        for (BingoTeam team : teams) {
            if (team.getMembers().contains(member)) {
                team.removeMember(member);
            }
        }
        teams.removeEmptyTeams();
    }
}


