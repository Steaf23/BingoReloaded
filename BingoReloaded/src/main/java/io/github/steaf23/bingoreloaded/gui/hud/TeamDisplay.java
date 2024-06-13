package io.github.steaf23.bingoreloaded.gui.hud;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.github.retrooper.packetevents.adventure.serializer.gson.GsonComponentSerializer;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.team.BasicTeamManager;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.util.BingoPlaceholderFormatter;
import io.github.steaf23.bingoreloaded.util.BingoReloadedPlaceholderExpansion;
import io.github.steaf23.bingoreloaded.util.ComponentConverter;
import io.github.steaf23.bingoreloaded.util.Message;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class TeamDisplay
{
    private record TeamInfo(String identifier, String displayName, @Nullable Component prefix, @Nullable Component suffix, Collection<String> entries) {}

    private final BingoSession session;
    private final TeamManager manager;
    // A Map of all teams created for each player, used when we have to remove all their teams when leaving or when removing empty teams
    private final Map<UUID, Set<TeamInfo>> createdTeams;

    public TeamDisplay(BingoSession session) {
        this.session = session;
        this.manager = session.teamManager;
        this.createdTeams = new HashMap<>();
    }

    public void update() {
        reset();

        Set<BingoTeam> activeTeams = manager.getActiveTeams().getTeams();
        for (Player player : session.getPlayersInWorld()) { // loop through all actual players.
            addTeamsForPlayer(player, activeTeams);
        }
    }

    /**
     * Creates new entry in teams map if the player was not present before.
     * @param player
     * @param activeTeams
     */
    public void addTeamsForPlayer(Player player, Set<BingoTeam> activeTeams) {
        // Compare the cached teams with the actual team manager's team state.
        // - If the manager doesn't have a team that was cached, it means we have to remove this team.

        Set<TeamInfo> knownTeams = createdTeams.getOrDefault(player.getUniqueId(), Set.of());
        for (TeamInfo t : knownTeams) {
            boolean removeTeam = !activeTeams.contains(t);

            if (removeTeam) {
                removeTeamForPlayer(t.identifier(), player);
            }
        }

        Set<TeamInfo> newTeams = activeTeams.stream().map(this::teamInfoFromBingoTeam).collect(Collectors.toSet());
        createdTeams.put(player.getUniqueId(), newTeams);
        for (TeamInfo team : newTeams) {
            createTeamForPlayer(team, player);
        }
    }

    public TeamInfo teamInfoFromBingoTeam(BingoTeam team) {
        TeamInfo info = new TeamInfo(team.getIdentifier(), team.getName(), ComponentConverter.bungeeComponentToAdventure(team.getPrefix()), null, team.getMemberNames());
        return info;
    }

    public void createTeamForPlayer(TeamInfo team, Player player) {
        //TODO: un-curse this team prefix shitshow & adventure component conversion
        WrapperPlayServerTeams.ScoreBoardTeamInfo info = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.text(team.displayName()),
                team.prefix(),
                team.suffix(),
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.ALWAYS,
                null,
                WrapperPlayServerTeams.OptionData.NONE
        );
        PacketWrapper packet = new WrapperPlayServerTeams(team.identifier(), WrapperPlayServerTeams.TeamMode.CREATE, info, team.entries());

        BingoReloaded.sendPacket(player, packet);
    }

    public void removeTeamForPlayer(String teamIdentifier, Player player) {
        PacketWrapper packet = new WrapperPlayServerTeams(teamIdentifier, WrapperPlayServerTeams.TeamMode.REMOVE, Optional.empty());
        BingoReloaded.sendPacket(player, packet);
    }

    public void clearTeamsForPlayer(@NotNull Player player) {
        for (TeamInfo info : createdTeams.getOrDefault(player.getUniqueId(), Set.of())) {
            removeTeamForPlayer(info.identifier(), player);
        }
        createdTeams.remove(player.getUniqueId());
    }

    public void reset() {
        for (Player player : session.getPlayersInWorld()) {
            clearTeamsForPlayer(player);
        }
        createdTeams.clear();
    }
}
