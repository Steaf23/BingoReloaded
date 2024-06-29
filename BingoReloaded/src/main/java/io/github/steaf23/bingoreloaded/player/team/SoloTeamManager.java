package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.placeholder.BingoPlaceholderFormatter;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.util.HSVLike;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;


import java.util.*;

/**
 * Similar to BasicTeamManager but each team can only have 1 member, the team's name being the name of the member.
 */
public class SoloTeamManager implements TeamManager
{
    private final BingoTeamContainer teams;
    private final BingoSession session;
    private final TeamData teamData;
    private final BingoTeam autoTeam;

    public SoloTeamManager(BingoSession session)
    {
        this.session = session;
        this.teamData = new TeamData();
        this.teams = new BingoTeamContainer();

        TextColor autoTeamColor = TextColor.fromHexString("#fdffa8");
        this.autoTeam = new BingoTeam("auto", autoTeamColor, BingoMessage.TEAM_AUTO.asPhrase(), createPrefix(autoTeamColor));
        this.teams.addTeam(autoTeam);
    }

    private Component createPrefix(TextColor color) {
        String prefixFormat = new BingoPlaceholderFormatter().getTeamFullFormat();
        Component prefix = LegacyComponentSerializer.legacySection().deserialize(BingoPlaceholderFormatter.createLegacyTextFromMessage(prefixFormat, color.toString(), "✦") + " ");
        return prefix;
    }

    @Nullable
    BingoParticipant getPlayer(BingoTeam team) {
        Optional<BingoParticipant> participant = team.getMembers().stream().findFirst();
        if (participant.isEmpty()) {
            ConsoleMessenger.error("Team " + LegacyComponentSerializer.legacySection().serialize(team.getColoredName()) + "does not have a player!");
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

    private TextColor determineTeamColor() {
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

        return TextColor.color(HSVLike.hsvLike(hue / 256.0f, 0.7f, 1.0f));
    }

    @Override
    public void setup() {
        teams.removeTeam(autoTeam);
        for (BingoParticipant participant : new HashSet<BingoParticipant>(autoTeam.getMembers())) {
            autoTeam.removeMember(participant);
            setupParticipant(participant);
        }
    }

    @Override
    public void reset() {
        for (BingoTeam team : new HashSet<BingoTeam>(teams.getTeams())) {
            for (BingoParticipant member : new HashSet<BingoParticipant>(team.getMembers())) {
                removeMemberFromTeam(member);
            }
            teams.removeTeam(team);
        }
        teams.addTeam(autoTeam);
    }

    public void setupParticipant(BingoParticipant participant) {
        TextColor teamColor = determineTeamColor();
        // create a team where the id is the same as the participant's id, which is good enough for our use case.
        BingoTeam team = new BingoTeam(participant.getId().toString(), teamColor, participant.getDisplayName(), createPrefix(teamColor));
        team.addMember(participant);
        teams.addTeam(team);

        participant.sessionPlayer().ifPresent(p -> {
            //FIXME: re-add
//            new TranslatedMessage(BingoTranslation.JOIN).color(ChatColor.GREEN)
//                    .arg(team.getColoredName())
//                    .send(p);
        });

        var joinEvent = new ParticipantJoinedTeamEvent(participant, team, session);
        Bukkit.getPluginManager().callEvent(joinEvent);
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
        if (session.isRunning()) {
            return false;
        }
        autoTeam.addMember(player);

        var joinEvent = new ParticipantJoinedTeamEvent(player, session);
        Bukkit.getPluginManager().callEvent(joinEvent);
        int memberCount = getParticipantCount();
        var countChangedEvent = new ParticipantCountChangedEvent(session, memberCount - 1, memberCount);

        player.sessionPlayer().ifPresent(p -> {
//            new TranslatedMessage(BingoTranslation.JOIN_AUTO).color(ChatColor.GREEN)
//                    .send(p);
        });
        return true;
    }

    @Override
    public boolean removeMemberFromTeam(@Nullable BingoParticipant member) {
        if (member == null) {
            return false;
        }

        removeMemberFromTeamSilently(member);
        var leaveEvent = new ParticipantLeftTeamEvent(member, session);
        Bukkit.getPluginManager().callEvent(leaveEvent);
        int memberCount = getParticipantCount();
        var countChangedEvent = new ParticipantCountChangedEvent(session, memberCount + 1, memberCount);

        member.sessionPlayer().ifPresent(player -> {
//            new TranslatedMessage(BingoTranslation.LEAVE).color(ChatColor.RED).send(player);
        });
        return true;
    }

    @Override
    public int getMaxTeamSize() {
        return 1;
    }

    @Override
    public int getTotalParticipantCapacity() {
        return Integer.MAX_VALUE;
    }


    @Override
    public void handlePlayerJoinedSessionWorld(PlayerJoinedSessionWorldEvent event) {
        ConsoleMessenger.log(ChatColor.GOLD + event.getPlayer().getDisplayName() + " joined world", session.getOverworld().getName());

        BingoParticipant participant = getPlayerAsParticipant(event.getPlayer());
        if (participant != null) {
            participant.sessionPlayer().ifPresent(player -> {
                if (!session.isRunning()) {
                    return;
                }
//                new TranslatedMessage(BingoTranslation.JOIN).color(ChatColor.GREEN)
//                        .arg(participant.getTeam().getColoredName())
//                        .send(player);
            });
            return;
        }

        if (session.isRunning()) {
            BingoMessage.NO_JOIN.sendToAudience(event.getPlayer());
            return;
        }

        if (getPlayerAsParticipant(event.getPlayer()) == null) {
            addMemberToTeam(new BingoPlayer(event.getPlayer(), session), "auto");
        }
    }

    @Override
    public void handlePlayerLeftSessionWorld(PlayerLeftSessionWorldEvent event) {
        ConsoleMessenger.log(ChatColor.GOLD + event.getPlayer().getDisplayName() + " left world", session.getOverworld().getName());
    }

    private void removeMemberFromTeamSilently(@NotNull BingoParticipant member) {
        for (BingoTeam team : teams) {
            team.removeMember(member);
        }
        teams.removeEmptyTeams("auto");
    }
}


