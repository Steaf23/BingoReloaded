package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
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
import net.kyori.adventure.util.HSVLike;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;

/**
 * Similar to BasicTeamManager but each team can only have 1 member, the team's name being the name of the member.
 */
public class SoloTeamManager implements TeamManager
{
    private final BingoTeamContainer teams;
    private final BingoSession session;
    private final BingoTeam autoTeam;

    public SoloTeamManager(BingoSession session) {
        this.session = session;
        this.teams = new BingoTeamContainer();

        TextColor autoTeamColor = TextColor.fromHexString("#fdffa8");
        if (autoTeamColor == null) {
            autoTeamColor = NamedTextColor.WHITE;
        }
        this.autoTeam = new BingoTeam("auto", autoTeamColor, BingoMessage.TEAM_AUTO.asPhrase(), createPrefix(autoTeamColor));
        this.teams.addTeam(autoTeam);
    }

    private Component createPrefix(TextColor color) {
        String prefixFormat = new BingoPlaceholderFormatter().getTeamFullFormat();
        return BingoMessage.createPhrase(prefixFormat.replace("{0}", "<" + color.toString() + ">").replace("{1}", "✦") + " ");
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
        for (BingoParticipant participant : new HashSet<>(autoTeam.getMembers())) {
            autoTeam.removeMember(participant);
            setupParticipant(participant);
        }
    }

    @Override
    public void reset() {
        for (BingoTeam team : new HashSet<>(teams.getTeams())) {
            for (BingoParticipant member : new HashSet<>(team.getMembers())) {
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

        BingoMessage.JOIN.sendToAudience(participant, NamedTextColor.GREEN, participant.getTeam().getColoredName());

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
     */
    @Override
    public boolean addMemberToTeam(BingoParticipant player, String teamId) {
        if (session.isRunning()) {
            return false;
        }
        autoTeam.addMember(player);

        var joinEvent = new ParticipantJoinedTeamEvent(player, session);
        Bukkit.getPluginManager().callEvent(joinEvent);

        BingoMessage.JOIN_AUTO.sendToAudience(player, NamedTextColor.GREEN);
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

        BingoMessage.LEAVE.sendToAudience(member, NamedTextColor.RED);
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

    @Override
    public void handlePlayerLeftSessionWorld(PlayerLeftSessionWorldEvent event) {
        ConsoleMessenger.log(event.getPlayer().displayName().append(Component.text(" left world")).color(NamedTextColor.GOLD), session.getOverworld().getName());
    }

    private void removeMemberFromTeamSilently(@NotNull BingoParticipant member) {
        for (BingoTeam team : teams) {
            team.removeMember(member);
        }
        teams.removeEmptyTeams("auto");
    }
}


